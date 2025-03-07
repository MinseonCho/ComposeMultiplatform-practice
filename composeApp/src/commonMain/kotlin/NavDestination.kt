interface NavDestination {
    val route: String

    data object Page : NavDestination {
        override val route: String = "page/{id}"

        fun createRoute(id: Int): String {
            return "page/$id"
        }
    }

    data object SavedUrl : NavDestination {
        override val route: String = "savedUrl"
    }
}
