# Auth (Firebase) Integration Helper

Este módulo conecta el `idToken` de Firebase con el interceptor HTTP de Retrofit para enviar `Authorization: Bearer <idToken>` en cada request.

## Componentes
- **AuthManager**: expone el proveedor de token al interceptor. No obtiene tokens.
- **AuthRepository**: cachea el token y ofrece métodos para login, refresh y logout.
- **RetrofitClient**: interceptor que añade el header de autorización si hay token (ya configurado).

## Pasos de Uso
1. **Inicializar al arrancar la app**
   - Ya hecho en `MainActivity.onCreate()`:
   ```kotlin
   AuthManager.init()
   ```

2. **Tras login de Firebase (ID token)**
   - Cuando tu flujo de Firebase obtenga el `idToken`, llama:
   ```kotlin
   AuthRepository.onLoginSuccess(idToken)
   ```

3. **Refresco automático de token**
   - Añade un listener de renovación para actualizar el token cuando Firebase lo renueve:
   ```kotlin
   val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
   auth.addIdTokenListener { firebaseAuth ->
       firebaseAuth.currentUser?.getIdToken(false)
           ?.addOnSuccessListener { result ->
               AuthRepository.onTokenRefreshed(result.token)
           }
   }
   ```

4. **Logout**
   - Limpiar el token y dejar de enviar el header:
   ```kotlin
   AuthRepository.onLogout()
   ```

## Notas
- Si el token es `null` o vacío, el interceptor NO enviará el header.
- No se modifica tu flujo de autenticación; solo se consume el token que ustedes gestionan.
- Mantén el `idToken` en memoria/Scope de app, no lo persistas sin cifrado.

## Ejemplo de Consumo de API
Los repositorios devuelven `NetworkResult<T>` ya desempaquetado del wrapper `ApiSuccess<T>`:
```kotlin
when (val result = productRepository.getProductsByBranch(branchId)) {
    is NetworkResult.Success -> {
        val productos = result.data
        // actualizar UI
    }
    is NetworkResult.Error -> {
        // mostrar mensaje result.message
    }
    is NetworkResult.Exception -> {
        // manejar excepciones de red
    }
}
```
