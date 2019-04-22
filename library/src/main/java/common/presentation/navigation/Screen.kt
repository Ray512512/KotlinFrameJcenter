package common.presentation.navigation

import android.support.v4.app.Fragment
import com.ray.frame.presentation.navigation.BackStrategy

/**
 */
data class Screen(val fragment: Fragment, val backStrategy: BackStrategy)