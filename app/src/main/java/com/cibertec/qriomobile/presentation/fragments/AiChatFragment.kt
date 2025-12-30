package com.cibertec.qriomobile.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cibertec.qriomobile.BuildConfig
import com.cibertec.qriomobile.databinding.FragmentAiChatBinding
import com.cibertec.qriomobile.presentation.adapters.ChatAdapter
import com.cibertec.qriomobile.presentation.adapters.ChatMessage
import com.cibertec.qriomobile.data.RetrofitClient
import com.cibertec.qriomobile.data.repository.ProductRepository
import com.cibertec.qriomobile.data.remote.NetworkResult
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class AiChatFragment : Fragment() {

    private var _binding: FragmentAiChatBinding? = null
    private val binding get() = _binding!!
    private val adapter = ChatAdapter()

    // Usar API Key desde BuildConfig
    private val apiKey = BuildConfig.GEMINI_API_KEY

    private var productsContext = ""
    private var iaReady = false

    private val args: AiChatFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.rvChat.layoutManager = LinearLayoutManager(context)
        binding.rvChat.adapter = adapter

        binding.btnSend.isEnabled = false

        loadProductsContext(args.branchId)

        binding.btnSend.setOnClickListener {
            val msg = binding.etMessage.text.toString()
            if (msg.isNotBlank()) {
                sendMessage(msg)
                binding.etMessage.text.clear()
            }
        }

        // Corregido: Llamar dentro de una corrutina para evitar bloqueo del hilo principal
        lifecycleScope.launch {
            listModelsWithRest()
        }
    }

    private fun loadProductsContext(branchId: Long) {
        lifecycleScope.launch {
            val repo = ProductRepository(RetrofitClient.api)
            when (val result = repo.getProductsByBranch(branchId)) {
                is NetworkResult.Success -> {
                    val menu = result.data.joinToString("\n") {
                        "- ${it.name}: S/ ${it.price} (${it.description.orEmpty()})"
                    }

                    productsContext = """
                        Eres un mesero experto y amable de la app Qrio.
                        MENÚ DISPONIBLE:
                        $menu
                        
                        Reglas:
                        - Responde solo con el menú
                        - Sugiere opciones
                        - Sé breve
                    """.trimIndent()

                    Log.d("AiChatFragment", "Prompt: $productsContext ")

                    iaReady = true
                    binding.btnSend.isEnabled = true

                    adapter.addMessage(
                        ChatMessage("¡Hola! Ya tengo el menú 😊 ¿Qué te provoca hoy?", false)
                    )
                }

                else -> {
                    Toast.makeText(context, "Error cargando menú", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendMessage(msg: String) {
        if (!iaReady) {
            adapter.addMessage(
                ChatMessage("⏳ Estoy cargando el menú, un momento...", false)
            )
            return
        }

        adapter.addMessage(ChatMessage(msg, true))
        adapter.addMessage(ChatMessage("⏳ Pensando...", false))
        val typingIndex = adapter.itemCount - 1

        lifecycleScope.launch {
            val prompt = "$productsContext\n\nCliente: $msg\nMesero:"
            // Corregido: Se usa la variable 'prompt' en lugar de un texto fijo
            val result = generateWithRest(prompt)
            
            Log.d("AiChatFragment", "Mensaje: $msg | Respuesta: $result")
            
            withContext(Dispatchers.Main) {
                adapter.removeAt(typingIndex)
                adapter.addMessage(
                    ChatMessage(
                        result ?: "Lo siento, no pude responder ahora 🙏",
                        false
                    )
                )
            }
        }
    }

    private suspend fun generateWithRest(prompt: String): String? =
        withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)   // 👈 CLAVE
                    .callTimeout(90, TimeUnit.SECONDS)   // 👈 CLAVE
                    .retryOnConnectionFailure(true)
                    .build()

                val url =
                    "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash-lite:generateContent?key=$apiKey"

                val bodyJson = JSONObject().apply {
                    put(
                        "contents",
                        org.json.JSONArray().put(
                            JSONObject().apply {
                                put("role", "user")
                                put(
                                    "parts",
                                    org.json.JSONArray().put(
                                        JSONObject().put("text", prompt)
                                    )
                                )
                            }
                        )
                    )
                }

                val body = bodyJson.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                client.newCall(request).execute().use { resp ->
                    val respBody = resp.body?.string()
                    Log.e("AiChatFragment", "HTTP ${resp.code} BODY: $respBody")

                    if (!resp.isSuccessful || respBody.isNullOrBlank()) {
                        return@withContext null
                    }

                    val json = JSONObject(respBody)
                    val parts = json
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")

                    buildString {
                        for (i in 0 until parts.length()) {
                            append(parts.getJSONObject(i).optString("text"))
                        }
                    }.ifBlank { null }
                }
            } catch (e: Exception) {
                Log.e("AiChatFragment", "Exception", e)
                null
            }
        }

    private fun ChatAdapter.removeAt(index: Int) {
        if (index in 0 until itemCount) {
            removeMessageAt(index)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Corregido: Función suspend para evitar NetworkOnMainThreadException
    private suspend fun listModelsWithRest(): String? = withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val url =
                "https://generativelanguage.googleapis.com/v1/models?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { resp ->
                val body = resp.body?.string()
                Log.e("AiChatFragment", "LIST MODELS ${resp.code}: $body")
                body
            }
        } catch (e: Exception) {
            Log.e("AiChatFragment", "ListModels error", e)
            null
        }
    }
}
