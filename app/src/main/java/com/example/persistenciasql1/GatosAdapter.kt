package com.example.t8_ej01_persistenciadatossqlite

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.persistenciasql1.R


// Define la clase DiscosAdapter, que extiende RecyclerView.Adapter y especifica MyViewHolder como su ViewHolder.
class GatosAdapter(private val gatosList: List<Gato>) : RecyclerView.Adapter<GatosAdapter.MyViewHolder>() {

    // Define la clase interna MyViewHolder, que extiende RecyclerView.ViewHolder.
    // Esta clase proporciona una referencia a las vistas de cada elemento de datos.
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Encuentra y almacena referencias a los elementos de la interfaz de usuario en el layout del ítem.
        var nombreDisco: TextView = view.findViewById(R.id.tvNombreGato)
        var anioPublicacion: TextView = view.findViewById(R.id.tvColorGato)
    }

    // Crea nuevas vistas (invocadas por el layout manager).
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Infla el layout del ítem de la lista (disco_item.xml) y lo pasa al constructor de MyViewHolder.
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.gato_item, parent, false)
        return MyViewHolder(itemView)
    }

    // Reemplaza el contenido de una vista (invocada por el layout manager).
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Obtiene el elemento de la lista de discos en esta posición.
        val gato = gatosList[position]
        // Reemplaza el contenido de las vistas con los datos del elemento en cuestión.
        holder.nombreDisco.text = gato.nombre
        holder.anioPublicacion.text = gato.color
    }

    // Devuelve el tamaño de la lista de datos (invocado por el layout manager).
    override fun getItemCount() = gatosList.size
}

