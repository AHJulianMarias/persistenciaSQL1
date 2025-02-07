package com.example.persistenciasql1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class CalculoFragment: Fragment(R.layout.fragment_calcular) {

    private lateinit var buttonHilos: Button
    private lateinit var buttonCorrutinas: Button
    private lateinit var textNumeros : EditText
    private lateinit var textResultado: TextView
    private lateinit var barraProgreso: ProgressBar
    private lateinit var buttonNotificacion: Button
    private val REQUEST_CODE_PERMISSIONS = 101
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
        buttonNotificacion = view.findViewById(R.id.buttonNoti)

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
        buttonNotificacion.setOnClickListener{
            if (checkPermissions()) {
                createNotificationChannel()
                // Si ya tiene permisos, mostrar notificación
                showNotification("¡Hola!", "Esta es una notificación instantánea")
                scheduleNotification() // Programar la segunda notificación
            } else {
                // Si no tiene permisos, pedirlos
                requestPermissions()
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "canal_notificaciones",
                "Canal de Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para mostrar notificaciones"
            }

            val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun scheduleNotification() {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(30, TimeUnit.SECONDS) // Espera 1 minuto
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireContext(), "canal_notificaciones")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icono de la notificación
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // La notificación desaparece al tocarla

        notificationManager.notify(1, builder.build()) // ID de la notificación
    }
    private fun checkPermissions(): Boolean {
        val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No es necesario pedir permiso en versiones anteriores
        }
        return notificationPermission
    }
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    // Maneja la respuesta del usuario cuando acepta o niega permisos
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, mostrar notificación
                showNotification("¡Permiso concedido!", "Ahora recibirás notificaciones.")
                scheduleNotification() // Programar la segunda notificación
            } else {
                // Permiso denegado, mostrar mensaje
                showNotification("Permiso denegado", "No puedes recibir notificaciones.")
            }
        }
    }
}