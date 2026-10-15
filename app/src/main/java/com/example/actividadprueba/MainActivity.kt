package com.example.actividadprueba

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.hardware.SensorEvent

class MainActivity : AppCompatActivity() {

    private var contador = 0
    private lateinit var sensorManager: SensorManager;
    private var sensorPasos: Sensor? = null;
    private lateinit var tvContador : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvContador = findViewById<TextView>(R.id.tvContador)
        val btnSumar = findViewById<Button>(R.id.btnSumar)
        val btnDetalle = findViewById<Button>(R.id.btnDetalle)

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACTIVITY_RECONGNITION), 100)
        }else{
            configurarSensor();
        }


        btnSumar.setOnClickListener {
            contador++
            tvContador.text = "Número de pasos: $contador"
        }

        btnDetalle.setOnClickListener {
            val intent = Intent(this, Detail::class.java);
            intent.putExtra("PASOS", cont);
            startActivity(intent);
        }
    }

    private fun configurarSensor(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager;
        sensorPasos = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if(sensorPasos == null){
            tvContador.text = "Error al configurar el sesnor de pasos";
        }
    }

    override fun onResume(){
        super.onResume()
        sensorPasos?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause(){
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    override fun onSensorChanged(event: SensorEvent){
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
            val pasosTotales = event.values[0].toInt();
            tvContador.text = "Pasos de hoy: $pasosTotales";
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int){

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                configurarSensor()
            } else {
                tvContador.text = "Permiso de sensor no concedido";
            }
        }
    }
}