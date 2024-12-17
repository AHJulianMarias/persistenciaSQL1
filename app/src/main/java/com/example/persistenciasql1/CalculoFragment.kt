package com.example.persistenciasql1

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalculoFragment: Fragment(R.layout.fragment_calcular) {

    private lateinit var buttonHilos: Button
    private lateinit var buttonCorrutinas: Button
    private lateinit var textNumeros : EditText
    private lateinit var textResultado: TextView
    private lateinit var barraProgreso: ProgressBar

    private var numero:Int = 0
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_calcular, container, false)

        buttonHilos = view.findViewById(R.id.buttonHilo)
        buttonCorrutinas = view.findViewById(R.id.buttonCorrutinas)
        textResultado = view.findViewById(R.id.textViewRes)
        textNumeros = view.findViewById(R.id.editTextNumber)
        barraProgreso = view.findViewById(R.id.progresoCalculo)


        buttonHilos.setOnClickListener{
            if(numeroValido(textNumeros.text.toString())){
                numero = textNumeros.text.toString().toInt()
                calcularNumeroPrimosConHilo(numero)
            }
        }
        buttonCorrutinas.setOnClickListener{
            if(numeroValido(textNumeros.text.toString())){
                numero = textNumeros.text.toString().toInt().toString().toInt()
                calcularNumeroPrimosConCorrutinas(numero)
            }

        }

        return view
    }

    private fun numeroValido(numeroStr: String): Boolean {
        try{
            numero = numeroStr.toInt()
        }catch (e: NumberFormatException){
            Toast.makeText(requireContext(),"Introduce un número válido", Toast.LENGTH_LONG).show()
            return false
        }
        if(numero <=0){
            Toast.makeText(requireContext(),"Introduce un número positivo", Toast.LENGTH_LONG).show()
            return false

        }
        return true

    }


    private fun calcularNumeroPrimosConHilo(numero :Int) {
        barraProgreso.visibility = android.view.View.VISIBLE
        barraProgreso.max = numero
        Thread{
            val primos = calcularPrimos(numero){progreso ->
                handler.post {
                    barraProgreso.progress = progreso
                }
            }
            handler.post {
                textResultado.text = primos.joinToString(", ")
            }
        }.start()
        barraProgreso.visibility = android.view.View.INVISIBLE
    }

    private fun calcularNumeroPrimosConCorrutinas(numero :Int) {
        barraProgreso.visibility = android.view.View.VISIBLE
        barraProgreso.max = numero
        CoroutineScope(Dispatchers.Main).launch {

            val primos = withContext(Dispatchers.Default) {
                calcularPrimos(numero){progreso->
                    launch(Dispatchers.Main) {
                        barraProgreso.progress = progreso
                    }
                }
            }
            textResultado.text = primos.joinToString(", ")
            barraProgreso.visibility = android.view.View.INVISIBLE
        }
    }

    private fun calcularPrimos(n: Int, callback:(Int) -> Unit): List<Int> {
        val primos = mutableListOf<Int>()
        var numero = 2

        while (primos.size < n) {
            if (esPrimo(numero)) {
                primos.add(numero)
                callback(primos.size)
            }
            numero++
        }
        return primos
    }

    private fun esPrimo(numero: Int): Boolean {
        if (numero < 2) return false
        for (i in 2 until numero) {
            if (numero % i == 0) {
                return false
            }
        }
        return true
    }
}