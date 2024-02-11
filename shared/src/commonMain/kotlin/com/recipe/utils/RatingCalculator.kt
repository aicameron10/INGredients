package com.recipe.utils


class RatingCalculator {
    companion object {
        fun calculateStars(rating: Double, maxStars: Int = 5): List<StarType> {
            val list = mutableListOf<StarType>()
            var remainingRating = rating

            for (i in 1..maxStars) {
                when {
                    remainingRating >= 1 -> {
                        list.add(StarType.FULL)
                        remainingRating -= 1
                    }
                    remainingRating >= 0.5 -> {
                        list.add(StarType.HALF)
                        remainingRating -= 0.5
                    }
                    else -> {
                        list.add(StarType.EMPTY)
                    }
                }
            }

            return list
        }
    }
}

enum class StarType {
    FULL, HALF, EMPTY
}