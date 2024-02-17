package com.example.keepmesafe

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.libsqlite.Report

class DangerAdapter(private var dangers: MutableList<Report>, private val context: Context, private val databaseHelper: DatabaseHelper) : RecyclerView.Adapter<DangerAdapter.DangerViewHolder>() {


    class DangerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDangerDescription: TextView = itemView.findViewById(R.id.tv_danger_description)
        val tvDangerLocation: TextView = itemView.findViewById(R.id.tv_danger_location)
        val imageButtonEdit: View = itemView.findViewById(R.id.imageButtonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DangerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyler_item_danger, parent, false)
        return DangerViewHolder(view)
    }

    override fun onBindViewHolder(holder: DangerViewHolder, position: Int) {
        val danger = dangers[position]
        holder.tvDangerDescription.text = danger.description
        holder.tvDangerLocation.text = "${danger.latitude}, ${danger.longitude}"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ViewMapActivity::class.java).apply {
                putExtra("LATITUDE", danger.latitude)
                putExtra("LONGITUDE", danger.longitude)
            }
            context.startActivity(intent)
        }

        holder.imageButtonEdit.setOnClickListener {
            val intent = Intent(context, ReportActivity::class.java).apply {
                putExtra("ID", danger.id)
                putExtra("DESCRIPTION", danger.description)
                putExtra("LATITUDE", danger.latitude)
                putExtra("LONGITUDE", danger.longitude)
            }
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            showDeleteConfirmationDialog(danger)
            true
        }
    }

    private fun showDeleteConfirmationDialog(danger: Report) {
        AlertDialog.Builder(context)
            .setTitle("Delete Danger")
            .setMessage("Are you sure you want to delete this danger?")
            .setPositiveButton("Yes") { _, _ ->
                databaseHelper.deleteReport(danger)
                updateDangers(databaseHelper.getAllReports())
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun updateDangers(newDangers: List<Report>) {
        dangers.clear()
        dangers.addAll(newDangers)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dangers.size
}