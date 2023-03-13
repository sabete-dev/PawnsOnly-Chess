import kotlin.math.*

fun main() {
    val chessBoard = mutableListOf(
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("8 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("7 | ","B"," | ","B"," | ","B"," | ","B"," | ","B"," | ","B"," | ","B"," | ","B"," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("6 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("5 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("4 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("3 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("2 | ","W"," | ","W"," | ","W"," | ","W"," | ","W"," | ","W"," | ","W"," | ","W"," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("1 | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," | "," "," |"),
        mutableListOf("  +-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+-","-","-+"),
        mutableListOf("    ","a","   ","b","   ","c","   ","d","   ","e","   ","f","   ","g","   ","h","  ")
    )

    val hasRecentMove = MutableList(8) { MutableList(8) {false} }

    println("Pawns-Only Chess")
    println("First Player's name:")
    val p1 = readln()
    println("Second Player's name:")
    val p2 = readln()

    printChessBoard(chessBoard)

    var turn = 1
    do {
        println("${if (turn % 2 != 0) p1 else p2}'s turn:")
        val color = if (turn % 2 != 0) "W" else "B"
        val input = readln()
        val coordinates = translateInput(input)

        if (input == "exit") print("Bye!")
        else if (!isValid(input)) println("Invalid Input")
        else if (!hasValidPawn(chessBoard, coordinates, color))
            println("No ${if (turn % 2 != 0) "White" else "Black"} pawn at ${input[0]}${input[1]}")
        else if (coordinates[0] == coordinates[2]) {
            if (!canMove(chessBoard, coordinates, color)) println("Invalid Input")
            else { // Successful Move
                chessBoard[coordinates[1]][coordinates[0]] = " "
                chessBoard[coordinates[3]][coordinates[2]] = color
                printChessBoard(chessBoard)
                resetHasRecentMove(hasRecentMove)
                hasRecentMove[simplify(coordinates[3])][simplify(coordinates[2])] = true
                turn++
            }
        } else if (abs(coordinates[0] - coordinates[2]) == 2) {
            if (chessBoard[coordinates[3]][coordinates[2]] != " ") {
                if (!canCapture(chessBoard, coordinates, color)) println("Invalid Input")
                else { // Successful Normal Capture
                    chessBoard[coordinates[1]][coordinates[0]] = " "
                    chessBoard[coordinates[3]][coordinates[2]] = color
                    printChessBoard(chessBoard)
                    resetHasRecentMove(hasRecentMove)
                    hasRecentMove[simplify(coordinates[3])][simplify(coordinates[2])] = true
                    turn++
                }
            } else if (chessBoard[coordinates[3]][coordinates[2]] == " ") {
                if (!canPassant(hasRecentMove, chessBoard, coordinates, color)) println("Invalid Input")
                else { // Successful En Passant Capture
                    chessBoard[coordinates[1]][coordinates[2]] = " "
                    chessBoard[coordinates[1]][coordinates[0]] = " "
                    chessBoard[coordinates[3]][coordinates[2]] = color
                    printChessBoard(chessBoard)
                    resetHasRecentMove(hasRecentMove)
                    hasRecentMove[simplify(coordinates[3])][simplify(coordinates[2])] = true
                    turn++
                }
            }
        } else println("Invalid Input")
    } while (input != "exit"
        && countWhite(chessBoard) != 0
        && countBlack(chessBoard) != 0
        && !rank8hasWhite(chessBoard)
        && !rank1hasBlack(chessBoard)
        && !isStalemate(hasRecentMove, chessBoard))

    when {
        countWhite(chessBoard) == 0 || rank1hasBlack(chessBoard) -> println("Black Wins!\nBye!")
        countBlack(chessBoard) == 0 || rank8hasWhite(chessBoard) -> println("White Wins!\nBye!")
        isStalemate(hasRecentMove, chessBoard) -> println("Stalemate!\nBye!")
    }
}

fun printChessBoard(x: MutableList<MutableList<String>>) {
    for (i in 0..17) {
        println(x[i].joinToString(""))
    }
}

fun resetHasRecentMove(x: MutableList<MutableList<Boolean>>) {
    for (i in x.indices) {
        for (j in x[i].indices) {
            x[i][j] = false
        }
    }
}

fun translateInput(rawInput: String): MutableList<Int> {
    return if (isValid(rawInput)) {
        val x = rawInput.chunked(1)
        mutableListOf(
            (x[0].first().code - 96) * 2 - 1,
            17 - x[1].toInt() * 2,
            (x[2].first().code - 96) * 2 - 1,
            17 - x[3].toInt() * 2
        )
    } else mutableListOf(0,0,0,0)
}

fun simplify(coo: Int): Int {
    return (coo/2+1)-1
}

fun isValid(x: String): Boolean {
    val regex = Regex("[a-h][1-8][a-h][1-8]")
    return regex.matches(x)
}

fun hasValidPawn(board: MutableList<MutableList<String>>, coo: MutableList<Int>, clr: String): Boolean {
    return (board[coo[1]][coo[0]] == clr)
}

fun canMove(board: MutableList<MutableList<String>>, coo: MutableList<Int>, clr: String): Boolean {
    val rank = if (clr == "W") 9 else 7
    return (board[coo[3]][coo[2]] == " ")
            && ((coo[3] == rank) || (coo[3] == coo[1]+(if (clr == "W") -2 else 2)))
            && (coo[0] == coo[2])
}

fun isStalemate(bool: MutableList<MutableList<Boolean>>, board: MutableList<MutableList<String>>): Boolean {
    val translatedCoo = listOf(1,3,5,7,9,11,13,15)
    var whiteCanMove = false
    var blackCanMove = false
    var whiteCancapture = false
    var blackCancapture = false
    var whiteCanpassant = false
    var blackCanPassant = false
    wLoop@ for (i in translatedCoo) {
        for (j in translatedCoo) {
            val square = board[i][j]
            if (square == "W") {
                if (!translatedCoo.contains(i - 2)) continue
                else if (board[i - 2][j] == " ") { // W Can Move
                    whiteCanMove = true
                    break@wLoop
                }

                if (!translatedCoo.contains(j - 2) || !translatedCoo.contains(i - 2)) continue
                else if (board[i - 2][j - 2] == "B") { // W Can Capture
                    whiteCancapture = true
                    break@wLoop
                }

                if (!translatedCoo.contains(j + 2) || !translatedCoo.contains(i - 2)) continue
                else if (board[i - 2][j + 2] == "B") { // W Can Capture
                    whiteCancapture = true
                    break@wLoop
                }

                if (!translatedCoo.contains(j + 2) || (1..8).contains(simplify(j) + 1)) continue
                else if (bool[simplify(i)][simplify(j) + 1] && board[i][j + 2] == "B") { // W Can Passant
                    whiteCanpassant = true
                    break@wLoop
                }

                if (!translatedCoo.contains(j - 2) || (1..8).contains(simplify(j) - 1)) continue
                if (bool[simplify(i)][simplify(j) - 1] && board[i][j - 2] == "B") { // W Can Passant
                    whiteCanpassant = true
                    break@wLoop
                }
            }
        }
    }
    bLoop@ for (i in translatedCoo) {
        for (j in translatedCoo) {
            val square = board[i][j]
            if (square == "B") {
                if (!translatedCoo.contains(i+2)) continue
                else if (board[i+2][j] == " ") { // B Can Move
                    blackCanMove = true
                    break@bLoop
                }

                if (!translatedCoo.contains(j-2) || !translatedCoo.contains(i-2)) continue
                else if (board[i-2][j-2] == "W") { // B Can Capture
                    blackCancapture = true
                    break@bLoop
                }

                if (!translatedCoo.contains(j-2) || !translatedCoo.contains(i+2)) continue
                else if (board[i+2][j-2] == "W") { // B Can Capture
                    blackCancapture = true
                    break@bLoop
                }

                if (!translatedCoo.contains(j+2) || (1..8).contains(simplify(j)+1)) continue
                else if (bool[simplify(i)][simplify(j)+1] && board[i][j+2] == "W") { // B Can Passant
                    blackCanPassant = true
                    break@bLoop
                }

                if (!translatedCoo.contains(j-2) || (1..8).contains(simplify(j)-1)) continue
                else if (bool[simplify(i)][simplify(j)-1] && board[i][j-2] == "W") { // B Can Passant
                    blackCanPassant = true
                    break@bLoop
                }
            }
        }
    }
    return (!whiteCanMove && !whiteCancapture && !whiteCanpassant) || (!blackCanMove && !blackCancapture && !blackCanPassant)
}

fun canCapture(board: MutableList<MutableList<String>>, coo: MutableList<Int>, clr: String): Boolean {
    return (board[coo[3]][coo[2]] == if (clr == "W") "B" else "W")
            && coo[3] == coo[1]+(if (clr == "W") -2 else 2)
}

fun canPassant(bool: MutableList<MutableList<Boolean>>,board: MutableList<MutableList<String>>, coo: MutableList<Int>, clr: String): Boolean {
    val x = (if (clr == "W") 2 else -2)
    return bool[simplify(coo[3])+x/2][simplify(coo[2])]
            && (board[coo[3]+x][coo[2]] == if (clr == "W") "B" else "W")
}

fun countBlack(board: MutableList<MutableList<String>>): Int {
    var blackPawn = 0
    for (i in 0..17) {
        for (j in 0..16) {
            if (board[i][j] == "B") blackPawn++
        }
    }
    return blackPawn
}

fun countWhite(board: MutableList<MutableList<String>>): Int {
    var whitePawn = 0
    for (i in 0..17) {
        for (j in 0..16) {
            if (board[i][j] == "W") whitePawn++
        }
    }
    return whitePawn
}

fun rank1hasBlack(board: MutableList<MutableList<String>>): Boolean {
    var hasBlack = false
    for (j in 1..16) {
        if (board[15][j] == "B") {
            hasBlack = true
            break
        }
    }
    return hasBlack
}

fun rank8hasWhite(board: MutableList<MutableList<String>>): Boolean {
    var hasWhite = false
    for (j in 1..16) {
        if (board[1][j] == "W") {
            hasWhite = true
            break
        }
    }
    return hasWhite
}