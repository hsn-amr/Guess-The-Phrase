package com.example.guessthephrase

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var rvMain: RecyclerView
    private lateinit var tvPhrese: TextView
    private lateinit var btnGuessPhrase: Button
    private lateinit var userInputPhrase: EditText
    private lateinit var btnGuessChar: Button
    private lateinit var userInputChar: EditText
    private var list = ArrayList<String>()
    private var listOfUsedChars = ""
    var countChar = 0
    var countPhrase = 0
    var doesWin = false
    val phreseList = listOf("I am Hassan", "Hail is good city")
    var originalPhrese = phreseList[Random.nextInt(phreseList.size)]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.mainLayout)
        rvMain = findViewById(R.id.rvMain)
        tvPhrese = findViewById(R.id.tvPhrese)
        btnGuessPhrase = findViewById(R.id.btnGuessPhrase)
        userInputPhrase = findViewById(R.id.etGuessPhrase)
        btnGuessChar = findViewById(R.id.btnGuessChar)
        userInputChar = findViewById(R.id.etGuessChar)

        rvMain.adapter = RecyclerViewAdapter(list)
        rvMain.layoutManager = LinearLayoutManager(this)

        tvPhrese.text = "Phrase: ${updateTextView(originalPhrese, listOfUsedChars)}"

        btnGuessChar.setOnClickListener {
            if(userInputChar.text.isNotEmpty()){
                countChar++
                var testChar = userInputChar.text.toString()[0]
                if(originalPhrese.toLowerCase().contains(testChar.toLowerCase()) && testChar.toLowerCase() !in listOfUsedChars) {
                    listOfUsedChars+=testChar.toLowerCase()
                    tvPhrese.text = "Phrase: ${updateTextView(originalPhrese, listOfUsedChars)}"
                    list.add("You Got a char")
                    list.add("${10 - countChar} guesses of chars")
                    rvMain.adapter!!.notifyDataSetChanged()
                    rvMain.smoothScrollToPosition(list.size)
                    if(!tvPhrese.text.contains("*")){
                        doesWin = true
                        gameOver(doesWin)
                    }
                }else if(testChar.toLowerCase() in listOfUsedChars){
                    Snackbar.make(mainLayout, "Please, Try other char", Snackbar.LENGTH_SHORT).show()
                }else{
                    list.add("Wrong guess: $testChar")
                    list.add("${10-countChar} guesses")
                    rvMain.adapter!!.notifyDataSetChanged()
                    rvMain.smoothScrollToPosition(list.size)
                    if(countChar >= 10) gameOver(doesWin)
                }
                userInputChar.text.clear()
            }else{
                Snackbar.make(mainLayout, "Please, Enter something", Snackbar.LENGTH_SHORT).show()
            }
        }

        btnGuessPhrase.setOnClickListener {
            if(userInputPhrase.text.isNotEmpty()){
                countPhrase++
                var testPhrase = userInputPhrase.text.toString().toLowerCase()
                if(originalPhrese.toLowerCase() == testPhrase){
                    tvPhrese.text = originalPhrese
                    list.add("You Got a phrase")
                    doesWin = true
                    rvMain.adapter!!.notifyDataSetChanged()
                    rvMain.smoothScrollToPosition(list.size)
                    gameOver(doesWin)
                }else{
                    list.add("Wrong guess: $testPhrase")
                    list.add("${10-countPhrase} guesses")
                    rvMain.adapter!!.notifyDataSetChanged()
                    rvMain.smoothScrollToPosition(list.size)
                    if(countPhrase>=10) gameOver(doesWin)
                }
                userInputPhrase.text.clear()
            }else{
                Snackbar.make(mainLayout, "Please, Enter something", Snackbar.LENGTH_SHORT).show()
            }
        }

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("list", list)
        outState.putInt("countChar", countChar)
        outState.putInt("countPhrase",countPhrase)
        outState.putBoolean("doesWin",doesWin)
        outState.putString("originalPhrese",originalPhrese)
        outState.putString("listOfUsedChars", listOfUsedChars)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        list = savedInstanceState.getStringArrayList("list")!!
        doesWin = savedInstanceState.getBoolean("doesWin")
        countChar = savedInstanceState.getInt("countChar")
        countPhrase = savedInstanceState.getInt("countPhrase")
        listOfUsedChars = savedInstanceState.getString("listOfUsedChars")!!
        originalPhrese = savedInstanceState.getString("originalPhrese", "")
        rvMain.adapter = RecyclerViewAdapter(list)
        rvMain.layoutManager = LinearLayoutManager(this)
        tvPhrese.text = "Phrase: ${updateTextView(originalPhrese, listOfUsedChars)}"
    }

    private fun gameOver(doesWin: Boolean){
        var message = if(doesWin) "You Win" else "You Lost"
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(message)
            .setPositiveButton("Restart Game", DialogInterface.OnClickListener{ _, _ -> recreate()})
            .setNegativeButton("Exit", DialogInterface.OnClickListener{ _, _ -> finish()})

        val alart = dialogBuilder.create()
        alart.setTitle("Game Over")
        alart.show()
        list.clear()
        listOfUsedChars =""
        countChar = 0
        countPhrase = 0
        this.doesWin = false
    }

    private fun updateTextView(text: String, chars: String): String{
        var result = ""
        for (i in text){
            when{
                i.toString().isBlank() -> result+=" "
                i.toLowerCase() in chars -> result+=i
                else -> result+="*"
            }
        }
        return result
    }
}

