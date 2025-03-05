package model

data class QueryItem(
    val id: Int,
    val key: String,
    val value: String,
    val isChecked: Boolean = true
) {
    companion object {
        fun generateEmptyQueryItem(id: Int): QueryItem = QueryItem(
            id = id,
            key = "",
            value = "",
            isChecked = false
        )
    }
}
