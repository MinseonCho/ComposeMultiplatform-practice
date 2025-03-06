interface NavDestination {
    val route: String

    data object Page : NavDestination {
        override val route: String = "page"
    }

    data object SavedUrl : NavDestination {
        override val route: String = "savedUrl"
    }
}
