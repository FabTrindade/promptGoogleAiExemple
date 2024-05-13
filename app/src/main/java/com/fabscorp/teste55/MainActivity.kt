package com.fabscorp.teste55

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class MainActivity : AppCompatActivity() {

    lateinit var askShowTextView: TextView
    lateinit var answerShowTextView: TextView
    lateinit var editText: EditText
    lateinit var sendButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Pergunte sobre as imagens"

        setContentView(R.layout.activity_main)

        askShowTextView = findViewById<TextView>(R.id.ask_show_TextView)
        answerShowTextView = findViewById<TextView>(R.id.answer_show_TextView)
        editText = findViewById<EditText>(R.id.ask_EditText)
        sendButton = findViewById<Button>(R.id.send_Button)

        val apiAiKey = resources.getString(R.string.google_ai_key)


        val model = GenerativeModel(
            "gemini-1.0-pro-vision-latest",
            // Retrieve API key as an environmental variable defined in a Build Configuration
            // see https://github.com/google/secrets-gradle-plugin for further instructions
            apiAiKey,
            generationConfig = generationConfig {
                temperature = 0.4f
                topK = 32
                topP = 1f
                maxOutputTokens = 4096
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
            ),
        )


        var  images: List<Bitmap> = listOf(
            R.drawable.image0,
            R.drawable.image1,
        ).map {
            BitmapFactory.decodeResource(baseContext.resources, it)
        }

        if (images.any { it == null }) {
            throw RuntimeException("Image(s) not found in resources")
        }


        sendButton.setOnClickListener {
            if (it.id == R.id.send_Button) {
                val prompt: String = editText.text.toString()

                editText.setText("AGUARDE! \n Resposta sendo gerada...")
                editText.isEnabled = false
                askShowTextView.text = ""
                answerShowTextView.text = ""
                hideKeyboard()
                sendButton.isEnabled = false


                lifecycleScope.launch {
                    val response = model.generateContent(
                        content() {
                            text(prompt + "\n")
                            image(images[0])
                            image(images[1])
                        }
                    )

                    askShowTextView.text = prompt
                    answerShowTextView.text = response.text
                    editText.setText("")
                    editText.isEnabled = true
                    sendButton.isEnabled = true
                }


            }
        }
    }

   private fun buttonClick (view: View) {

    }
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }
}