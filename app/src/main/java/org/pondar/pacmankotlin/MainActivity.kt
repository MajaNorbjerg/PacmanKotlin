package org.pondar.pacmankotlin

import android.content.ClipData
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.OnClickListener
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import org.pondar.pacmankotlin.databinding.ActivityMainBinding
import android.content.Context
import android.view.View
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener {

    //reference to the game class.
    private lateinit var game: Game
    private lateinit var binding: ActivityMainBinding

    private var pacmanTimer: Timer = Timer()
    private var counterTimer: Timer = Timer()
    private var countdownTimer: Timer = Timer()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //makes sure it always runs in portrait mode - will cost a warning
        //but this is want we want!
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        Log.d("onCreate", "Oncreate called")

        game = Game(this, binding.pointsView, binding)

        //intialize the game view clas and game class
        game.setGameView(binding.gameView)
        binding.gameView.setGame(game)
        game.newGame()

        // Button onclick
        //--------------------------------------------------------------------------------
//        binding.moveLeft.setOnClickListener {
//            game.direction = game.LEFT
//
//        }
//        binding.moveUp.setOnClickListener {
//            game.direction = game.UP
//
//        }
//        binding.moveDown.setOnClickListener {
//            game.direction = game.DOWN
//
//        }
//        binding.moveRight.setOnClickListener {
//            game.direction = game.RIGHT
//
//        }
//        binding.resetButton.setOnClickListener(this)
        binding.stopButton.setOnClickListener(this)
        binding.startButton.setOnClickListener(this)

        // Swipe gestures
        //--------------------------------------------------------------------------------
        view.setOnTouchListener(object : OnSwipeTouchListener(applicationContext) {

            override fun onSwipeTop() {
                game.direction = game.UP
            }

            override fun onSwipeBottom() {
                game.direction = game.DOWN
            }

            override fun onSwipeLeft() {
                game.direction = game.LEFT
            }

            override fun onSwipeRight() {
                game.direction = game.RIGHT
            }
        })


        pacmanTimer.schedule(object : TimerTask() {
            override fun run() {
                pacmanTimerMethod()
            }
        }, 0, 130)

        counterTimer.schedule(object : TimerTask() {
            override fun run() {
                counterTimerMethod()
            }
        }, 0, 1000)

        countdownTimer.schedule(object : TimerTask() {
            override fun run() {
                countdownTimerMethod()
            }
        }, 0, 1000)

    }

    private fun counterTimerMethod() {
        this.runOnUiThread(counterTimerTick)
    }
    private fun countdownTimerMethod() {
        this.runOnUiThread(countdownTimerTick)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_LONG).show()

            game.currentLevel = game.levels[0]
            game.newGame()
            game.counter = 60
            binding.textView.text = "Timer value: ${game.counter}"
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private val counterTimerTick = Runnable {
        if (game.running) {
            if (game.counter > 0 && game.coins.filter { !it.taken }.any()) {
                game.counter--

            } else {
                Toast.makeText(this, "Game over you didnt make it", Toast.LENGTH_LONG).show()
                game.running = false
                game.gameOver = true
            }
        }
        else{
            Log.d("countdown", "${game.counter}")
        }
        binding.textView.text = "Timer value: ${game.counter}"
    }


    override fun onStop() {
        super.onStop()
        //just to make sure if the app is killed, that we stop the timer.
        pacmanTimer.cancel()
        counterTimer.cancel()
    }

    private fun pacmanTimerMethod() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer - i.e the background

        //we could do updates here TO GAME LOGIC,
        // but not updates TO ACTUAL UI

        //We call the method that will work with the UI
        //through the runOnUiThread method.

        this.runOnUiThread(timerTick)
        //timerTick.run() //try doing this instead of the above...will crash the app!

    }


    private val timerTick = Runnable {
        //This method runs in the same thread as the UI.
        // so we can draw
        if (game.running) {
            when (game.pacBitmap) {
                game.pacResizedBitmap -> { game.pacBitmap = game.pacResizedBitmap2 }
                game.pacResizedBitmap2 -> { game.pacBitmap = game.pacResizedBitmap3 }
                game.pacResizedBitmap3 -> { game.pacBitmap = game.pacResizedBitmap }
                else -> { game.pacBitmap = game.pacResizedBitmap}
            }
            if( game.enemyBitmap == game.enemyResizedBitmap){ game.enemyBitmap = game.enemyResizedBitmap2 }else{ game.enemyBitmap = game.enemyResizedBitmap}

            game.movePacman(game.direction, game.currentLevel.pacSpeed)
            game.moveEnemies(game.currentLevel.enemySpeed)

        }
    }


    private val countdownTimerTick = Runnable {
//        if (game.running) {
//            if (game.counter > 0 && game.coins.filter { !it.taken }.any()) {
//                game.counter--
//
//            } else {
//                Toast.makeText(this, "Game over you didnt make it", Toast.LENGTH_LONG).show()
//                game.running = false
//                game.gameOver = true
//            }
//        }
//        else{
//            Log.d("countdown", "${game.counter}")
//        }
//        binding.textView.text = "Timer value: ${game.counter}"
    }


    //if anything is pressed - we do the checks here
    override fun onClick(v: View) {
        if (v.id == R.id.startButton) {
            if (!game.gameOver)
                game.running = true
        } else if (v.id == R.id.stopButton) {
            game.running = false
        }
//        else if (v.id == R.id.action_newGame) {
//            counter = 60
//            game.newGame()
//            game.running = true
//
//            binding.textView.text = "Timer value: $counter"
//
//        }
    }
}
