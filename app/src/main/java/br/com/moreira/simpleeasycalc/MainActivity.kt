package br.com.moreira.simpleeasycalc

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var inputField: EditText
    private var currentExpression = "0" // Start with 0
    private var op1: Double? = null
    private var pendingOp: String = "="
    private var isNewOp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the status bar color to match the app's color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        }

        inputField = findViewById(R.id.result)
        inputField.setText(currentExpression) // Set input field to 0 on start

        // Define the number buttons
        val button0: Button = findViewById(R.id.button0)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)
        val button4: Button = findViewById(R.id.button4)
        val button5: Button = findViewById(R.id.button5)
        val button6: Button = findViewById(R.id.button6)
        val button7: Button = findViewById(R.id.button7)
        val button8: Button = findViewById(R.id.button8)
        val button9: Button = findViewById(R.id.button9)
        val buttonDot: Button = findViewById(R.id.buttonDot)

        // Define the operation buttons
        val buttonAdd: Button = findViewById(R.id.buttonAdd)
        val buttonSubtract: Button = findViewById(R.id.buttonSubtract)
        val buttonMultiply: Button = findViewById(R.id.buttonMultiply)
        val buttonDivide: Button = findViewById(R.id.buttonDivide)
        val buttonEqual: Button = findViewById(R.id.buttonEqual)

        // Define the clear and backspace buttons
        val buttonClear: Button = findViewById(R.id.buttonClear)
        val backSpace: ImageButton = findViewById(R.id.backSpace)

        // Toggle sign button
        val buttonToggleSign: Button = findViewById(R.id.buttonBracket)

        // Percentage button
        val buttonPercentage: Button = findViewById(R.id.buttonPercentage)

        // Set click listeners for number buttons
        val numberButtons = listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
        numberButtons.forEach { button ->
            button.setOnClickListener {
                appendToExpression(button.text.toString())
            }
        }

        // Listeners for operations
        buttonAdd.setOnClickListener { setPendingOperation("+", buttonAdd) }
        buttonSubtract.setOnClickListener { setPendingOperation("-", buttonSubtract) }
        buttonMultiply.setOnClickListener { setPendingOperation("*", buttonMultiply) }
        buttonDivide.setOnClickListener { setPendingOperation("/", buttonDivide) }

        // Listeners for clear and equals
        buttonClear.setOnClickListener { clear() }
        buttonEqual.setOnClickListener { calculateResult() }

        // Listener for backspace button
        backSpace.setOnClickListener {
            if (currentExpression.isNotEmpty()) {
                currentExpression = currentExpression.dropLast(1)
                inputField.setText(currentExpression)
            }
        }

        // Listener for decimal point button
        buttonDot.setOnClickListener { appendToExpression(".") }

        // Listener for toggle sign (±) button
        buttonToggleSign.setOnClickListener {
            toggleSign()
        }

        // Listener for percentage button
        buttonPercentage.setOnClickListener {
            calculatePercentage()
        }
    }

    private fun appendToExpression(value: String) {
        if (isNewOp) {
            currentExpression = ""
            isNewOp = false
        }

        // Prevent starting with multiple zeros
        if (currentExpression == "0" && value != ".") {
            currentExpression = value
        } else {
            currentExpression += value
        }
        inputField.setText(currentExpression)
    }

    private fun setPendingOperation(op: String, button: Button) {
        if (op1 == null) {
            op1 = currentExpression.toDoubleOrNull()
        } else {
            // Now calculation will happen only when "=" is pressed
            calculateResult()
        }
        pendingOp = op
        isNewOp = true

        // Change the color of the pressed operation button
        resetOperationButtonColor()
        button.setTextColor(getColor(R.color.black)) // Change text color to black
    }

    private fun calculateResult() {
        // This is where the actual calculation is done
        try {
            val value = currentExpression.toDoubleOrNull()
            if (value != null && op1 != null) {
                val result = when (pendingOp) {
                    "+" -> op1!! + value
                    "-" -> op1!! - value
                    "*" -> op1!! * value
                    "/" -> if (value == 0.0) throw UnsupportedOperationException("Cannot divide by zero") else op1!! / value
                    else -> value
                }

                // Check if the result is an integer or decimal
                if (result == result.toInt().toDouble()) {
                    // If the result is an integer, display it as an integer
                    inputField.setText(String.format(Locale.getDefault(), "%d", result.toInt()))
                    currentExpression = result.toInt().toString()
                } else {
                    // If it's decimal, display it as double
                    inputField.setText(String.format(Locale.getDefault(), "%.2f", result))
                    currentExpression = result.toString()
                }

                isNewOp = true
                op1 = result

                // Reset the operation button colors to white
                resetOperationButtonColor() // Adding the call to restore the button colors
            }
        } catch (e: Exception) {
            inputField.setText("Error")
            resetOperationButtonColor() // Also reset colors in case of error
        }
    }

    private fun clear() {
        currentExpression = "0" // Reset to 0
        op1 = null
        pendingOp = "="
        inputField.setText(currentExpression)
        resetOperationButtonColor()
        isNewOp = true
    }

    private fun calculatePercentage() {
        try {
            val value = currentExpression.toDoubleOrNull()
            if (value != null) {
                val percentage = value / 100
                inputField.setText(String.format(Locale.getDefault(), "%.2f", percentage))
                currentExpression = percentage.toString()
            }
        } catch (e: Exception) {
            inputField.setText("Error")
        }
    }

    private fun toggleSign() {
        if (currentExpression.isNotEmpty()) {
            val currentValue = currentExpression.toDoubleOrNull()
            if (currentValue != null) {
                currentExpression = (-currentValue).toString()
                inputField.setText(currentExpression)
            }
        }
    }

    private fun resetOperationButtonColor() {
        val operationButtons = listOf(
            findViewById<Button>(R.id.buttonAdd), findViewById<Button>(R.id.buttonSubtract),
            findViewById<Button>(R.id.buttonMultiply), findViewById<Button>(R.id.buttonDivide)
        )
        // Reset the text color of operation buttons to white
        operationButtons.forEach { button ->
            button.setTextColor(getColor(R.color.white))
        }
    }
}