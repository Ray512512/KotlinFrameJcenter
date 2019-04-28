package common.presentation.navigation

import common.presentation.utils.Experimental

/**
 */
@Experimental
sealed class BackStrategy {

    object KEEP : BackStrategy()
    object DESTROY : BackStrategy()
}