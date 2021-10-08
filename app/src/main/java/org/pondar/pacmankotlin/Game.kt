package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import org.pondar.pacmankotlin.databinding.ActivityMainBinding
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView, binding: ActivityMainBinding) {

    private var pointsView: TextView = view
    private var points: Int = 0


    // Directions
    // ---------------------------------------------
    val RIGHT = 1
    val DOWN = 2
    val LEFT = 3
    val UP = 4
    var direction = RIGHT

    // Gamestate
    // ---------------------------------------------
    var running = true
    var gameOver = false
    var counter: Int = 60


    // bitmaps
    // ---------------------------------------------

    // pacman
    var pacBitmap: Bitmap
    var pacResizedBitmap: Bitmap
    var pacResizedBitmap2: Bitmap
    var pacResizedBitmap3: Bitmap

    var pacx: Int = 0
    var pacy: Int = 0


    // coin
    var coinBitmap: Bitmap
    var coinResizedBitmap: Bitmap

    // enemy
    var enemyBitmap: Bitmap
    var enemyResizedBitmap: Bitmap
    var enemyResizedBitmap2: Bitmap


    //did we initialize the coins?
    lateinit var coin: GoldCoin
    var coinsInitialized = false
    var enemiesInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()
    var enemies = ArrayList<Enemy>()

    var levels = ArrayList<Level>()
    lateinit var currentLevel: Level

    //a reference to the gameview
    private lateinit var gameView: GameView
    private var h: Int = 0
    private var w: Int = 0 //height and width of screen


    private val handler = Handler(Looper.getMainLooper())


    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        var pacDiameter = 150
        pacResizedBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pacman
            ), pacDiameter, pacDiameter, true
        )
        pacResizedBitmap2 = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pacman2
            ), pacDiameter, pacDiameter, true
        )
        pacResizedBitmap3 = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pacman3
            ), pacDiameter, pacDiameter, true
        )


        coinResizedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)

        pacBitmap = pacResizedBitmap
        coinBitmap = Bitmap.createScaledBitmap(coinResizedBitmap, 100, 100, true)

        var enemyDiameter = 100
        enemyResizedBitmap = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.redenemy
            ), enemyDiameter, enemyDiameter, true
        )
        enemyResizedBitmap2 = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.redenemy2
            ), enemyDiameter, enemyDiameter, true
        )

        enemyBitmap = enemyResizedBitmap

        for (i in 1..3) {
            levels.add(Level(i, 2 + i, 30, 10+(i*2), 2+i))
        }
        currentLevel = levels[0]

    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins() {
        //DO Stuff to initialize the array list with some coins.
        // coins = coin.coinArray()
        println("Try and getGAMGE with: ${gameView.w}, height: ${gameView.h}")
        coins.clear()
//        println("GameView height: ${gameView.height}, ${gameView.width}")
        var coinMaxX: Int = gameView.w - coinBitmap.width
        var coinMaxY: Int = gameView.h - coinBitmap.height
        var coinRangeX = (0..coinMaxX)
        var coinRangeY = (0..coinMaxY)
        println("coinMAXX: $coinMaxX, coinMAXY: $coinMaxY")

        for (i in 0 until currentLevel.numberOfCoins)
            coins.add(GoldCoin(coinRangeX.random(), coinRangeY.random()))
//
//        for (coin in coins)
//        println("Coinslist ${coin.coinX}, ${coin.coinY}")
        coinsInitialized = true
    }


    //TODO initialize enemies
    fun initializeEnemies() {
        //DO Stuff to initialize the array list with some coins.
        // coins = coin.coinArray()
        println("Try and get game view with: ${gameView.width}, height: ${gameView.height}")
        enemies.clear()
//
        var MaxX: Int = gameView.w - enemyBitmap.width
        var MaxY: Int = gameView.h - enemyBitmap.height
        var setEnemyX = 0
        var setEnemyY = 0
        var RangeX = (0..MaxX)
        var RangeY = (0..MaxY)
        var pacCenterX = pacx + pacBitmap.width / 2
        var pacCenterY = pacy + pacBitmap.height / 2

        for (i in 0 until currentLevel.numberOfEnemies) {

            setEnemyX = RangeX.random()
            setEnemyY = RangeX.random()
            var distToPac = calculateDistance(
                pacCenterX,
                pacCenterY,
                setEnemyX + enemyBitmap.width / 2,
                setEnemyY + enemyBitmap.height / 2
            )

            while (distToPac < 500){
                setEnemyX = RangeX.random()
                setEnemyY = RangeX.random()
                distToPac = calculateDistance(
                    pacCenterX,
                    pacCenterY,
                    setEnemyX + enemyBitmap.width / 2,
                    setEnemyY + enemyBitmap.height / 2
                )
            }
            enemies.add(Enemy(setEnemyX, setEnemyY))
        }
//
//        for (coin in coins)
//        println("Coinslist ${coin.coinX}, ${coin.coinY}")
        enemiesInitialized = true
    }


    fun newGame() {
        gameOver = false
        //running = true
        direction = RIGHT
        pacx = 50
        pacy = 400 //just some starting coordinates - you can change this.
        //reset the points
        coinsInitialized = false
        enemiesInitialized = false
        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"

        if (gameView.w != 0 && gameView.h != 0) {
            initializeGoldcoins()
            initializeEnemies()
        }
        gameView.invalidate() //redraw screen

        Toast.makeText(context, "You are now at level ${currentLevel.levelNumber} - watch out for more enemies", Toast.LENGTH_LONG).show()

        handler.postDelayed({
            running = true
        }, 2000)
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun movePacman(direction: Int, pixels: Int) {
        when (direction) {
            RIGHT -> {
                if (pacx + pixels + pacBitmap.width < w)
                    pacx = pacx + pixels

            }
            DOWN -> {
                if (pacy + pixels + pacBitmap.height < h)
                    pacy = pacy + pixels
            }
            LEFT -> {
                if (pacx - pixels + pacBitmap.width > 0 + pacBitmap.width)
                    pacx = pacx - pixels

            }

            UP -> {
                if (pacy - pixels + pacBitmap.height > 0 + pacBitmap.height)
                    pacy = pacy - pixels
            }
        }
        doCoinCollisionCheck()
        gameView.invalidate()
    }

    fun moveEnemies(pixels: Int) {
        for (enemy in enemies) {

            var horisontalDirection: Int = if (pacx > enemy.enemyX) RIGHT
            else LEFT
            var verticalDirection: Int = if (pacy > enemy.enemyY) DOWN
            else UP

            var calculatedDirection: Int =
                if (abs(pacx - enemy.enemyX) > abs(pacy - enemy.enemyY)) horisontalDirection else verticalDirection

            var randomDirection = (1..4).random()
            when (calculatedDirection) {
                RIGHT -> {
                    if (enemy.enemyX + pixels + enemyBitmap.width < w)
                        enemy.enemyX += pixels
                }
                DOWN -> {
                    if (enemy.enemyY + pixels + enemyBitmap.height < h)
                        enemy.enemyY += pixels
                }
                LEFT -> {
                    if (enemy.enemyX - pixels + enemyBitmap.width > 0 + enemyBitmap.width)
                        enemy.enemyX -= pixels
                }
                UP -> {
                    if (enemy.enemyY - pixels + enemyBitmap.height > 0 + enemyBitmap.height)
                        enemy.enemyY -= pixels
                }
            }
        }
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman
    fun doCoinCollisionCheck() {
        var coinsTaken = 0
        for (coin in coins) {
            if (!coin.taken) {
                var pacCenterX = pacx + pacBitmap.width / 2
                var pacCenterY = pacy + pacBitmap.height / 2
                var dist = calculateDistance(
                    pacCenterX,
                    pacCenterY,
                    coin.coinX + coinBitmap.width / 2,
                    coin.coinY + coinBitmap.height / 2
                )
                if (dist < pacBitmap.width / 2) {
                    points++
                    pointsView.text = "${context.resources.getString(R.string.points)} $points"
                    coin.taken = true
                }
            }

        }

        for (enemy in enemies) {
            var pacCenterX = pacx + pacBitmap.width / 2
            var pacCenterY = pacy + pacBitmap.height / 2
            var dist = calculateDistance(
                pacCenterX,
                pacCenterY,
                enemy.enemyX + enemyBitmap.width / 2,
                enemy.enemyY + enemyBitmap.height / 2
            )
            if (dist < enemyBitmap.width / 2) {
                gameOver = true
                running = false
                Toast.makeText(context, "You got eaten by a redface", Toast.LENGTH_LONG).show()
            }
            //fdsdkjfldsk
        }

        if (coins.filter { !it.taken }.isNullOrEmpty()) {
            if (currentLevel == levels[levels.size - 1]) {
                gameOver = true
                running = false
                println("You win")
                Toast.makeText(context, "You win", Toast.LENGTH_LONG).show()
            } else {
                gameOver = true
                running = false
                var currentIndex = levels.indexOf(currentLevel)
                currentLevel = levels[currentIndex + 1]
                // Set game level to current + 1
                newGame()
                counter = 60
                //binding.textView.text = "Timer value: $counter"
            }

        }
    }

    private fun calculateDistance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        // calculate distance and return it

        var c =
            sqrt(((x1.toFloat() - x2.toFloat()).pow(2f)) + ((y1.toFloat() - y2.toFloat()).pow(2f))).toFloat()
        println("c is with *: ${c}")
        return c
    }


}