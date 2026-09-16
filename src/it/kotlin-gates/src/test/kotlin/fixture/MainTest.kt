package fixture

class MainTest {
        fun  badlyFormatted( ) {
            val x   = Main().ok()
        require(x == 1)
    }
}
