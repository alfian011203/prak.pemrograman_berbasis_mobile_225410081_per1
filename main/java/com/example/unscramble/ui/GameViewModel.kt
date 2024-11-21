package com.example.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unscramble.data.MAX_NO_OF_WORDS
import com.example.unscramble.data.SCORE_INCREASE
import com.example.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//TODO ViewModel yang berisi data aplikasi dan metode untuk memproses data
class GameViewModel : ViewModel() {

    // TODO Status UI state
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    // TODO Seperangkat kata yang digunakan dalam permainan
    private var usedWords: MutableSet<String> = mutableSetOf()
    private lateinit var currentWord: String

    init {
        resetGame()
    }

    // TODO Menginisialisasi ulang data permainan untuk memulai ulang permainan.

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    // TODO Perbarui tebakan pengguna

    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

      // TODO Memeriksa apakah tebakan pengguna benar.

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {

            // TODO Tebakan pengguna benar, tingkatkan skor
            // TODO memanggil updateGameState() untuk mempersiapkan permainan untuk putaran berikutnya

            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            // TODO Tebakan pengguna salah, tampilkan kesalahan
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }
        // TODO Setel ulang tebakan pengguna
        updateUserGuess("")
    }


    // TODO Langsung ke kata berikutnya
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reset user guess
        updateUserGuess("")
    }


      // TODO Memilih currentWord dan currentScrambledWord baru dan memperbarui UiState sesuai dengan currentWord. game state.

    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            // TODO Babak terakhir dalam permainan, perbarui isGameOver menjadi benar, jangan pilih kata baru
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else{
            // TODO Babak normal dalam permainan
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // TODO Mengacak kata
        tempWord.shuffle()
        while (String(tempWord) == word) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    private fun pickRandomWordAndShuffle(): String {
        // TODO Terus ambil kata acak baru sampai Anda menemukan kata yang belum pernah digunakan sebelumnya
        currentWord = allWords.random()
        return if (usedWords.contains(currentWord)) {
            pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            shuffleCurrentWord(currentWord)
        }
    }
}
