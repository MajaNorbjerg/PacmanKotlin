package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.nfc.Tag
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import org.pondar.pacmankotlin.databinding.ActivityMainBinding
import java.util.ArrayList
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.roundToInt


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
    var halfPacWidth = 0
    var halfPacHeight = 0

    var pacGoFurtherUp = true
    var pacGoFurtherDown = true
    var pacGoFurtherRight = true
    var pacGoFurtherLeft = true


    // coin
    lateinit var coinBitmap: Bitmap
    lateinit var coinResizedBitmap: Bitmap

    // enemy
    var enemyBitmap: Bitmap
    var enemyResizedBitmap: Bitmap
    var enemyResizedBitmap2: Bitmap


    //did we initialize the coins?
    lateinit var coin: GoldCoin
    var coinsInitialized = false
    var enemiesInitialized = false
    var wallsInitialized = false
    var pacInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()
    var enemies = ArrayList<Enemy>()
    var walls = ArrayList<Wall>()

    var levels = ArrayList<Level>()
    lateinit var currentLevel: Level


    var wallBitmap: Bitmap

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
            ), pacDiameter.toInt(), pacDiameter.toInt(), true
        )
        pacResizedBitmap2 = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pacman2
            ), pacDiameter.toInt(), pacDiameter.toInt(), true
        )
        pacResizedBitmap3 = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pacman3
            ), pacDiameter.toInt(), pacDiameter.toInt(), true
        )
        pacBitmap = pacResizedBitmap
        halfPacWidth = pacBitmap.width / 2
        halfPacHeight = pacBitmap.height / 2

//        coinResizedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)
//
//
//        coinBitmap = Bitmap.createScaledBitmap(coinResizedBitmap, 100, 100, true)

        wallBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wall)
        wallBitmap = Bitmap.createScaledBitmap(wallBitmap, pacBitmap.width, pacBitmap.height, true)

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

        for (i in 1..10) {
            levels.add(Level(i, 9 + i, 33, 10, i, 3 + i))
        }
        currentLevel = levels[0]

    }


    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins() {

        coinResizedBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.coin)


        coinBitmap = Bitmap.createScaledBitmap(coinResizedBitmap, 100, 100, true)
        //DO Stuff to initialize the array list with some coins.
        // coins = coin.coinArray()
        println("Try and getGAMGE with: ${gameView.w}, height: ${gameView.h}")
        coins.clear()
//        println("GameView height: ${gameView.height}, ${gameView.width}")
        var coinMaxX: Int = gameView.w - coinBitmap.width
        var coinMaxY: Int = gameView.h - coinBitmap.height
        var RangeX = (0..coinMaxX)
        var RangeY = (0..coinMaxY)

        var pacCenterX = pacx + pacBitmap.width / 2
        var pacCenterY = pacy + pacBitmap.height / 2


//        for (i in 0 until currentLevel.numberOfCoins)
//            coins.add(GoldCoin(RangeX.random(), RangeY.random()))


        for (i in 0 until currentLevel.numberOfCoins) {
            var setX = RangeX.random()
            var setY = RangeY.random()

            var awayFromWalls = walls.none {
                calculateDistance(
                    it.X + wallBitmap.width / 2,
                    it.Y + wallBitmap.height / 2,
                    setX + coinBitmap.width / 2,
                    setY + coinBitmap.height / 2
                ) < wallBitmap.width
            }
            while (!awayFromWalls) {
                setX = RangeX.random()
                setY = RangeY.random()
                awayFromWalls = walls.none {
                    calculateDistance(
                        it.X + wallBitmap.width / 2,
                        it.Y + wallBitmap.height / 2,
                        setX + coinBitmap.width / 2,
                        setY + coinBitmap.height / 2
                    ) < wallBitmap.width
                }
            }
            coins.add(GoldCoin(setX, setY))
        }
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

            while (distToPac < 500) {
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
        enemiesInitialized = true
    }


    fun initializeWalls() {

        walls.clear()

        var MaxX: Int = gameView.w
        var MaxY: Int = gameView.h
        var rangeX = (0..MaxX)
        var rangeY = (0..MaxY)
        var intervalX = wallBitmap.width
        var intervalY = wallBitmap.width

        for (i in 0 until currentLevel.numberOfWalls)
            walls.add(
                Wall(
                    (intervalX * ((rangeX.random() / intervalX.toFloat())).roundToInt()),
                    (intervalY * ((rangeY.random() / intervalY.toFloat())).roundToInt())
                )
            )
        wallsInitialized = true
    }

    fun newGame() {

        if (gameOver) {
            points = 0
        }

        gameOver = false
        direction = RIGHT

        pacx = 50
        pacy = 400 //just some starting coordinates - you can change this.

        //reset the points
        coinsInitialized = false
        enemiesInitialized = false

        pointsView.text = "${context.resources.getString(R.string.points)} $points"

        if (gameView.w != 0 && gameView.h != 0) {
            initializeWalls()
            initializeGoldcoins()
            initializeEnemies()
        }

        gameView.invalidate() //redraw screen
        if (currentLevel.levelNumber != 1)
            Toast.makeText(
                context,
                "You are now at level ${currentLevel.levelNumber} - watch out for more enemies",
                Toast.LENGTH_LONG
            ).show()

        handler.postDelayed({
            running = true
        }, 2000)
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }


    fun movePacman(direction: Int) {
        var pixels = currentLevel.pacSpeed

        when (direction) {
            RIGHT -> {
                if (pacx + pixels + pacBitmap.width <= w && pacGoFurtherRight)
                    pacx += pixels
                else if (pacx + pixels + pacBitmap.width > w && pacGoFurtherRight)
                    pacx = w - pacBitmap.width
            }
            DOWN -> {
                if (pacy + pixels + pacBitmap.height <= h && pacGoFurtherDown)
                    pacy += pixels
                else if (pacy + pixels + pacBitmap.height > h && pacGoFurtherDown)
                    pacy = h - pacBitmap.height
            }
            LEFT -> {
                if (pacx - pixels + pacBitmap.width >= 0 + pacBitmap.width && pacGoFurtherLeft)
                    pacx -= pixels
                else if (pacx - pixels + pacBitmap.width < 0 + pacBitmap.width && pacGoFurtherLeft)
                    pacx = 0
            }
            UP -> {
                if (pacy - pixels + pacBitmap.height >= 0 + pacBitmap.height && pacGoFurtherUp)
                    pacy -= pixels
                else if (pacy - pixels + pacBitmap.height < 0 + pacBitmap.height && pacGoFurtherUp)
                    pacy = 0
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

            var randomDirection = (1..4).random() //Another type of movement
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

        var pacCenterX = pacx + pacBitmap.width / 2
        var pacCenterY = pacy + pacBitmap.height / 2
        var pacUpX = pacx + pacBitmap.width / 2
        var pacUpY = pacy
        var pacDownX = pacx + pacBitmap.width / 2
        var pacDownY = pacy + pacBitmap.height
        var pacRightX = pacx + pacBitmap.width
        var pacRightY = pacy + pacBitmap.height / 2
        var pacLeftX = pacx
        var pacLeftY = pacy + pacBitmap.height / 2


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
                Toast.makeText(context, "You got eaten by a purpleface", Toast.LENGTH_LONG)
                    .show()
            }

        }

        if (coins.filter { !it.taken }.isNullOrEmpty()) {
            if (currentLevel == levels[levels.size - 1]) {
                gameOver = true
                running = false
                println("You win")
                Toast.makeText(context, "You win", Toast.LENGTH_LONG).show()
            } else {
                gameOver = false
                running = false
                var currentIndex = levels.indexOf(currentLevel)
                currentLevel = levels[currentIndex + 1]
                newGame()
                counter = 60
            }

        }


        var minDistToPac = 20

        // Can go up?
        // ----------------------------------------------------
        wallLoop@ for (wall in walls) {
            var wallDownY = wall.Y + wallBitmap.height
            var wallRightX = wall.X + wallBitmap.width
            var wallLeftX = wall.X
            for (x in wall.X until wall.X + wallBitmap.width) {
                if (abs(wallDownY - pacUpY) < minDistToPac && pacCenterX > wallLeftX - pacBitmap.width / 2 && pacCenterX < wallRightX + pacBitmap.width / 2) {
                    pacy = wallDownY
                    pacGoFurtherUp = false
                    break@wallLoop
                } else {
                    pacGoFurtherUp = true
                }
            }
        }

        // Can go down?
        // ----------------------------------------------------
        wallLoop@ for (wall in walls) {
            var wallUpY = wall.Y
            var wallRightX = wall.X + wallBitmap.width
            var wallLeftX = wall.X
            for (x in wall.X until wall.X + wallBitmap.width) {
                if (abs(wallUpY - pacDownY) < minDistToPac && pacCenterX > wallLeftX - pacBitmap.width / 2 && pacCenterX < wallRightX + pacBitmap.width / 2) {
                    pacy = wallUpY - pacBitmap.height
                    pacGoFurtherDown = false
                    break@wallLoop
                } else {
                    pacGoFurtherDown = true
                }
            }
        }


        // Can go right?
        // ----------------------------------------------------
        wallLoop@ for (wall in walls) {
            var wallUpY = wall.Y
            var wallDownY = wall.Y + wallBitmap.height
            var wallLeftX = wall.X
            for (y in wall.Y until wall.Y + wallBitmap.height) {
                if (abs(wallLeftX - pacRightX) < minDistToPac && pacCenterY > wallUpY - pacBitmap.height / 2 && pacCenterY < wallDownY + pacBitmap.height / 2) {
                    pacx = wallLeftX - pacBitmap.width
                    pacGoFurtherRight = false
                    break@wallLoop
                } else {
                    pacGoFurtherRight = true
                }
            }
        }

        // Can go left?
        // ----------------------------------------------------
        wallLoop@ for (wall in walls) {
            var wallUpY = wall.Y
            var wallDownY = wall.Y + wallBitmap.height
            var wallRightX = wall.X + wallBitmap.width
            for (y in wall.Y until wall.Y + wallBitmap.height) {
                if (abs(wallRightX - pacLeftX) < minDistToPac && pacCenterY > wallUpY - pacBitmap.height / 2 && pacCenterY < wallDownY + pacBitmap.height / 2) {
                    pacx = wallRightX
                    pacGoFurtherLeft = false
                    break@wallLoop
                } else {
                    pacGoFurtherLeft = true
                }
            }
        }


    }


    private fun calculateDistance(x1: Int, y1: Int, x2: Int, y2: Int): Float {
        // calculate distance and return it

        return hypot(x1.toFloat() - x2.toFloat(), y1.toFloat() - y2.toFloat())

        // Old calculation method
//        var c =
//            sqrt(((x1.toFloat() - x2.toFloat()).pow(2f)) + ((y1.toFloat() - y2.toFloat()).pow(2f))).toFloat()
//        println("c is with *: ${c}")
//        return c
    }


}