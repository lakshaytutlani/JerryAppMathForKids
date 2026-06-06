package com.example.ui.components

import kotlin.random.Random

data class MathChallenge(
    val problem: String,
    val solutionHint: String,
    val correctValue: Int,
    val rewardStars: Int = 3,
    val rewardCheese: Int = 1
)

object MathChallengeGenerator {
    fun generateQuestion(grade: Int): MathChallenge {
        val rand = Random.nextInt(1, 6)
        return when (grade) {
            3 -> { 
                when (rand) {
                    1 -> MathChallenge(
                        problem = "I have 14 cheddar cubes in my mouse hole, and Cousin Tuffy brings me 17 more! 🧀 How many cheese cubes do we have in total? (Hint: Try adding 10 + 10 first, then 4 + 7!)",
                        solutionHint = "Add 14 + 17 together. Think about 14 + 10 = 24, now add 7 more!",
                        correctValue = 31
                    )
                    2 -> MathChallenge(
                        problem = "Tom built a sneaky mouse trap using 45 paperclips! 🖇️ I managed to chew and break 18 of them. How many paperclips are still working in the trap? (Hint: Subtract 18 from 45!)",
                        solutionHint = "Calculate 45 - 18. Take away 10 first to get 35, then take away 8!",
                        correctValue = 27
                    )
                    3 -> MathChallenge(
                        problem = "Cousin Tuffy is very hungry and wants to eat 4 snack boxes. If each snack box has 12 tasty cheese crackers inside, how many crackers is he going to eat? (Hint: Multiply 4 times 12!)",
                        solutionHint = "Calculate 4 * 12. Think about adding 12 + 12 + 12 + 12!",
                        correctValue = 48
                    )
                    4 -> MathChallenge(
                        problem = "Jerry's mouse tunnel has 6 segments. Each segment has 9 little glowing fireflies to light my way! 🪰 How many fireflies do I have in my tunnel in total in the end?",
                        solutionHint = "Do you know what 6 groups of 9 equals? Think of 6 times 9!",
                        correctValue = 54
                    )
                    else -> MathChallenge(
                        problem = "I found 80 crumbs of delicious chocolate cookies! 🍪 If I pack them into tiny sacks with 10 crumbs in each sack, how many sacks can I completely fill?",
                        solutionHint = "Divide 80 by 10. How many tens are there in 80?",
                        correctValue = 8
                    )
                }
            }
            4 -> { 
                when (rand) {
                    1 -> MathChallenge(
                        problem = "I cut a delicious giant round Swiss cheese wheel into 8 equal slices! 🧀 If I eat 3 slices and Cousin Tuffy eats 2 slices, how many slices are LEFT for tomorrow?",
                        solutionHint = "We started with 8 slices. We ate 3 and then 2 slices. Tell me how many are left!",
                        correctValue = 3
                    )
                    2 -> MathChallenge(
                        problem = "Cousin Tuffy is sharing cookie crumbs. He has 36 crumbs and divides them equally into 4 small mouse-pockets. How many crumbs go in each pocket?",
                        solutionHint = "Divide 36 by 4. What number multiplied by 4 gives you 36?",
                        correctValue = 9
                    )
                    3 -> MathChallenge(
                        problem = "My favorite cheese factory has 12 boxes, and each box contains 15 golden cheese packages. 📦 How many golden cheese packages do they have in total?",
                        solutionHint = "Calculate 12 * 15. Think: 10 * 15 = 150, now add 2 * 15!",
                        correctValue = 180
                    )
                    4 -> MathChallenge(
                        problem = "Tom is sitting 120 centimeters away from my mouse hole. I can take giant leaps of 8 centimeters each! 🐭 How many leaps do I need to reach Tom's tail to pinch it?",
                        solutionHint = "Divide 120 by 8. Think: What is 120 split down into half, and then split again?",
                        correctValue = 15
                    )
                    else -> MathChallenge(
                        problem = "Tuffy collected 7 crates of cheese slices. Each crate holds 24 slices! 🧀 How many total cheese slices did he collect?",
                        solutionHint = "Calculate 7 * 24. Think: 7 * 20 = 140, and 7 * 4 = 28!",
                        correctValue = 168
                    )
                }
            }
            5 -> { 
                when (rand) {
                    1 -> MathChallenge(
                        problem = "My secret cheese-running pathway goes around a rectangular desk that is 15 inches long and 10 inches wide. 📏 If I run all the way around the outer board edge once, how many inches do I run in total?",
                        solutionHint = "Find the boundary/perimeter! Add all 4 sides: 15 + 10 + 15 + 10!",
                        correctValue = 50
                    )
                    2 -> MathChallenge(
                        problem = "If 4 boxes of gourmet cheddar cost $24, how many dollars would 9 identical boxes cost? (Hint: Find the cost of 1 box first!)",
                        solutionHint = "Divide 24 by 4 to find the price of 1 box, then multiply that by 9!",
                        correctValue = 54
                    )
                    3 -> MathChallenge(
                        problem = "I need to share a batch of 144 mini cheese wheels equally among 12 mouse families. 🐭 How many mini cheese wheels will each family get?",
                        solutionHint = "Divide 144 by 12. Think of 12 * 12!",
                        correctValue = 12
                    )
                    4 -> MathChallenge(
                        problem = "Our mouse hole holds a cheese supply. Supposing we eat 14 blocks of cheese every single week. If we have 98 blocks of cheese stored, how many WEEKS will our supply last?",
                        solutionHint = "Divide 98 by 14. What number multiplied by 14 equals 98?",
                        correctValue = 7
                    )
                    else -> MathChallenge(
                        problem = "I can crawl through 15 meters of mouse tunnels in 3 minutes. 🐭 At this exact speed, how many METERS of tunnel can I explore in 10 minutes?",
                        solutionHint = "First find meters per minute: 15 meters divided by 3 minutes. Then multiply that speed by 10!",
                        correctValue = 50
                    )
                }
            }
            else -> { 
                when (rand) {
                    1 -> MathChallenge(
                        problem = "The ratio of cheddar slices to Swiss cheese slices in my pouch is 4 to 3! 🧀 If I have exactly 24 Swiss slices, how many CHEDDAR slices do I have?",
                        solutionHint = "The ratio is 4 cheddar : 3 Swiss. If Swiss = 24 (which is 3 * 8), multiply cheddar's part (4) by the same multiplier 8!",
                        correctValue = 32
                    )
                    2 -> MathChallenge(
                        problem = "An algebraic puzzle: My mouse tunnel length satisfies the formula: 3x - 12 = 48 meters. Can you help me find the secret value of x?",
                        solutionHint = "To solve 3x - 12 = 48, add 12 to both sides to get 3x = 60. Then divide by 3!",
                        correctValue = 20
                    )
                    3 -> MathChallenge(
                        problem = "I want to paint my triangular mouse-shelter entrance. The baseline width is 12 inches, and the height is 15 inches. What is the area of my entrance in square inches?",
                        solutionHint = "Triangle area is (Base * Height) / 2. Work out (12 * 15) and divide it in halves!",
                        correctValue = 90
                    )
                    4 -> MathChallenge(
                        problem = "Jerry's running speed is 6 feet per second. If Tom is exactly 72 feet behind me and starts chasing, but I make it to my safe mousehole in 12 seconds, how many FEET do I run in those 12 seconds?",
                        solutionHint = "Multiply 6 feet per second by 12 seconds!",
                        correctValue = 72
                    )
                    else -> MathChallenge(
                        problem = "We had 50 pieces of gourmet cheese crumbs. Little Tuffy ate 40% of them! 🧀 How many pieces of cheese crumbs did Tuffy eat?",
                        solutionHint = "Find 40% of 50. This is the same as 50 * 0.40, or 40 * (50/100)!",
                        correctValue = 20
                    )
                }
            }
        }
    }
}
