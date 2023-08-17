package invest.megalo.data

class PropertyListData(
    var propertyId: String,
    var address: String,
    var imageUrls: String,
    var description: String,
    var valueUsd: Double,
    var percentageAvailable: Double,
    var monthlyEarningUsd: Double,
    var sizeSf: Int,
    var valueAverageAnnualChangePercentage: Double
)