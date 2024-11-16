import CalculationInterface

class CalculationImpl : CalculationInterface {
    override fun calculateSum(data: List<Int>): Int {
        return data.sum()
    }

    override fun calculateAverage(data: List<Int>): Double {
        return data.average()
    }

    override fun calculateCount(data: List<Any>): Int {
        return data.size
    }
}