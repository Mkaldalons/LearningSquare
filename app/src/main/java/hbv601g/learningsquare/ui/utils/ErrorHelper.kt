package hbv601g.learningsquare.ui.utils

import android.view.View
import android.widget.TextView

object ErrorHelper {
    //Maybe move error handling here

    // Check if email address is valid, etc..
    fun validateInput(textInputs: List<String>): String
    {
        if(!textInputs.all { it.isNotEmpty() })
        {
            return "Please fill all fields"
        }
        else if(textInputs[3] == textInputs[4])
        {
            return "Passwords do not match"
        }
        return "Valid"
    }

    // Dæmi..Sjáum til
    fun displayError(errorTextView: TextView, view: View, message: String)
    {
        errorTextView.visibility = View.VISIBLE
        errorTextView.text = message
        errorTextView.postDelayed({
            errorTextView.visibility = View.GONE
        }, 5_000)
    }
}
