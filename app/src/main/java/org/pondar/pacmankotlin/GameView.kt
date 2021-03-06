package org.pondar.pacmankotlin

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


//note we now create our own view class that extends the built-in View class
class GameView : View {

    private lateinit var game: Game
    public var h: Int = 0
    public var w: Int = 0 //used for storing our height and width of the view

    fun setGame(game: Game) {
        this.game = game
    }


    /* The next 3 constructors are needed for the Android view system,
	when we have a custom view.
	 */
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //In the onDraw we put all our code that should be
    //drawn whenever we update the screen.
    override fun onDraw(canvas: Canvas) {
        //Here we get the height and weight
        h = height
        w = width
        //update the size for the canvas to the game.
        game.setSize(h, w)
        Log.d("GAMEVIEW", "h = $h, w = $w")


        //are the coins initiazlied?
        //if not initizlise them
        if (!(game.wallsInitialized))
            game.initializeWalls()
        if (!(game.coinsInitialized))
            game.initializeGoldcoins()
        if (!(game.enemiesInitialized))
            game.initializeEnemies()


        //Making a new paint object
        val paint = Paint()
        canvas.drawColor(Color.rgb(173, 216, 230)) //clear entire canvas to white color

        //TODO loop through the list of goldcoins and draw them here

        for (i in 0 until game.walls.size) {

            canvas.drawBitmap(
                game.wallBitmap,
                game.walls[i].X.toFloat(),
                game.walls[i].Y.toFloat(),
                paint
            )
        }

        for (i in 0 until game.coins.size) {
            if (!game.coins[i].taken) {
                canvas.drawBitmap(
                    game.coinBitmap,
                    game.coins[i].coinX.toFloat(),
                    game.coins[i].coinY.toFloat(),
                    paint
                )
            }
        }


        for (i in 0 until game.enemies.size) {
            if (game.enemies[i].isAlive) {
                canvas.drawBitmap(
                    game.enemyBitmap,
                    game.enemies[i].enemyX.toFloat(),
                    game.enemies[i].enemyY.toFloat(),
                    paint
                )
            }
        }


        //draw the pacman
        canvas.drawBitmap(
            game.pacBitmap, game.pacx.toFloat(),
            game.pacy.toFloat(), paint
        )

//        canvas.drawBitmap(game.pacBitmap, game.rotator, paint)


        game.doCoinCollisionCheck()
        super.onDraw(canvas)
    }

}
