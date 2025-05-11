package dev.mk.translatorapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dev.mk.translatorapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TranslationViewModel by viewModels()

    private var isEnglishToMyanmar = true // Default mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Translator (DeepSeek API)"


        setupUI()
        observeViewModel()

    }
    fun copyText() {

        val text = binding.etOutputText.text

        val clipboard: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(this, "Text copied!", Toast.LENGTH_SHORT).show()
    }

    fun shareText() {
        val text = binding.etOutputText.text

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun setupUI() {

        binding.shareId.setOnClickListener {
           shareText()
        }
        binding.copyId.setOnClickListener {
           copyText()
        }

        checkEngMM()

        binding.btnTranslate.setOnClickListener {
            val inputText = binding.etInputText.text.toString()
            if (isEnglishToMyanmar) {
                viewModel.translateText("Translate to Myanmar: $inputText") // English to Myanmar
            } else {
                viewModel.translateText("Translate to English: $inputText") // Myanmar to English
            }
            binding.etOutputText.text   =  Editable.Factory.getInstance().newEditable("")

        }

        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            isEnglishToMyanmar = isChecked
            binding.switchLanguage.text = if (isChecked) {
                "English to Myanmar"
            } else {
                "Myanmar to English"
            }
            checkEngMM()
        }
    }
    fun checkEngMM()
    {
        if(isEnglishToMyanmar){
            binding.etInputText.hint  = Editable.Factory.getInstance().newEditable("Enter English Text")
            binding.etOutputText.hint  = "ဘာသာပြန်ထားသောစာားများဒီ နေရာတွင်မကြာခင်ပြသပါမည်"

        }else{
            binding.etInputText.hint  = Editable.Factory.getInstance().newEditable("စာသားထည့်ပါ")
            binding.etOutputText.hint  = "Translated English text will be appear here"

        }
    }

    private fun observeViewModel() {
        viewModel.translationResult.observe(this) { result ->
            when (result) {
                is TranslationResult.Success -> {
                    binding.progressBar.stopAutoLoop()
                    binding.progressBar.visibility = View.GONE
                    binding.etOutputText.text = Editable.Factory.getInstance().newEditable(result.translatedText)
                }
                is TranslationResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                is TranslationResult.Loading -> {
                    binding.progressBar.startAutoLoop()
                    binding.progressBar.visibility = View.VISIBLE
                    // Handle loading state
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.context_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> {
                binding.etInputText.text = Editable.Factory.getInstance().newEditable("")
                binding.etOutputText.text = Editable.Factory.getInstance().newEditable("")
                checkEngMM()
                Toast.makeText(this, "Clear ", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}