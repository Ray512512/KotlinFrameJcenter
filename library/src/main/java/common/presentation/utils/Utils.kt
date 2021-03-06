package common.presentation.utils

import android.app.ActivityManager
import android.content.Context
import android.view.View
import common.presentation.kotlinx.extensions.onClick
import java.io.*
import java.util.ArrayList

/**
 * Created by Ray on 2018/3/21.
 */
class Utils {
    companion object {
        fun saveObject(path: String, saveObject: Any) {
            var fos: FileOutputStream? = null
            var oos: ObjectOutputStream? = null
            val f = File(path)
            try {
                fos = FileOutputStream(f)
                oos = ObjectOutputStream(fos)
                oos.writeObject(saveObject)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    oos?.close()
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun restoreObject(path: String): Any? {
            var fis: FileInputStream? = null
            var ois: ObjectInputStream? = null
            val f = File(path)
            if (!f.exists()) {
                return null
            }
            try {
                fis = FileInputStream(f)
                ois = ObjectInputStream(fis)
                return ois.readObject()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    ois?.close()
                    fis?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
            return null
        }
        fun getCurrentProcessName(context: Context):String{
            val pid = android.os.Process.myPid()
            var processName = ""
            val manager = context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (i in  manager.runningAppProcesses){
                if(i.pid == pid){
                    processName=i.processName
                    break
                }
            }
            return processName;
        }

        @JvmStatic
        fun onClick(v: View, function: () -> Unit){
            v.onClick { function() }
        }

        @JvmStatic
        fun isInstalled(context: Context, packageName: String): Boolean {
            val packageManager = context.packageManager
            val packageInfos = packageManager.getInstalledPackages(0)
            val packageNames = ArrayList<String>()

            if (packageInfos != null) {
                for (i in packageInfos.indices) {
                    val packName = packageInfos[i].packageName
                    packageNames.add(packName)
                }
            }
            return packageNames.contains(packageName)
        }
    }


}