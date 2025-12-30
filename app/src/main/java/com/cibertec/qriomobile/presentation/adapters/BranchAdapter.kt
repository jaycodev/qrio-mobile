package com.cibertec.qriomobile.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.qriomobile.R
import com.cibertec.qriomobile.data.model.BranchDto

class BranchAdapter(
    private val branches: List<BranchDto>,
    private val onClick: (BranchDto) -> Unit
) : RecyclerView.Adapter<BranchAdapter.BranchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_branch, parent, false)
        return BranchViewHolder(view)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(branches[position])
    }

    override fun getItemCount() = branches.size

    inner class BranchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtName: TextView = itemView.findViewById(R.id.txtBranchName)
        private val txtAddress: TextView = itemView.findViewById(R.id.txtBranchAddress)

        fun bind(branch: BranchDto) {
            txtName.text = branch.name
            txtAddress.text = branch.address ?: "Sin dirección"
            
            itemView.setOnClickListener {
                onClick(branch)
            }
        }
    }
}
