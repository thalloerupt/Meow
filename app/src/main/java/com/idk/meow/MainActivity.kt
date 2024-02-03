package com.idk.meow

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.drawable.toBitmap
import com.idk.meow.Shamiko.isWhitelistModeOn
import com.idk.meow.Shamiko.toggleWhitelist
import com.idk.meow.ui.theme.MeowTheme
import kotlinx.coroutines.delay
import java.io.IOException
import java.util.regex.Pattern

//全写在一起，汗流浃背了吧
open class MainActivity : ComponentActivity() {
   // private val p: Process = Runtime.getRuntime().exec("su")
    private lateinit var pm: PackageManager

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pm = packageManager









        haveRoot()


        setContent {

            var resolvedInfos by remember {
                mutableStateOf<List<ApplicationInfo>>(getAppList(false))

            }


            MeowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    var presses by remember { mutableIntStateOf(0) }
                    var string by remember { mutableStateOf("") }
                    var mUid by remember { mutableIntStateOf(0) }
                    var mName by remember { mutableStateOf("") }

                    var searchExpanded by remember {
                        mutableStateOf(false)
                    }
                    var isChecked by remember {
                        mutableStateOf(false)
                    }

                    var menuExpanded by remember {
                        mutableStateOf(false)
                    }
                    var selected by remember { mutableStateOf(false) }
                    var isShow by remember { mutableStateOf(false) }

                    SideEffect {
                        isChecked = isWhitelistModeOn
                    }

                    Scaffold(
                        topBar = {




                            TopAppBar(
                                colors = topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary,
                                ),
                                title = {
                                    Text("Meow")
                                },
                                actions = {
                                    if (searchExpanded) {
                                        val focusRequester = remember { FocusRequester() }
                                        val keyboard = LocalSoftwareKeyboardController.current
                                        /*LaunchedEffect(key1 = searchExpanded, block = {
                                            focusRequester.requestFocus()//首次进入和重组页面请求焦点
                                            keyboard?.show()//首次进入页面弹出键盘，注意必须先获取焦点才能弹出键盘成功
                                        })*/
                                        SearchBar(
                                            modifier = Modifier
                                                .focusRequester(focusRequester)
                                                .padding(8.dp),
                                            query = string,
                                            onQueryChange = {
                                                resolvedInfos = search(it,selected)
                                                string = it
                                            },
                                            onSearch = {},
                                            active = false,
                                            enabled = true,
                                            onActiveChange = {},
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = {
                                                        string = ""
                                                        searchExpanded = !searchExpanded
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Clear,
                                                        contentDescription = "clear",
                                                    )
                                                }
                                            }
                                        ) {

                                        }

                                        //focusRequester.requestFocus()

                                    }



                                    IconButton(onClick = { searchExpanded = !searchExpanded }) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = "more",
                                        )
                                    }

                                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "more",
                                        )
                                    }


                                    DropdownMenu(
                                        expanded = menuExpanded,
                                        onDismissRequest = { menuExpanded = false },
                                    ) {
                                        // 6
                                        DropdownMenuItem(
                                            text = {
                                                Text(stringResource(id = R.string.refresh))
                                            },
                                            onClick = {
                                                //resolvedInfos = getAppList()
                                                menuExpanded = !menuExpanded




                                            },
                                        )


                                        FilterChip(
                                            modifier = Modifier.fillMaxWidth(),
                                            onClick = {
                                                selected = !selected
                                                //menuExpanded = !menuExpanded
                                                resolvedInfos = getAppList(selected)


                                            },
                                            label = {
                                                Text("显示系统应用")
                                            },
                                            selected = selected,
                                            leadingIcon = if (selected) {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Filled.Done,
                                                        contentDescription = "Done icon",
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }
                                            } else {
                                                {
                                                    Icon(
                                                        imageVector = Icons.Filled.Clear,
                                                        contentDescription = "Done icon",
                                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                                    )
                                                }

                                            },
                                        )




                                        /*DropdownMenuItem(
                                            text = {
                                                Text(stringResource(id = R.string.about))
                                            },
                                            onClick = { *//* TODO *//* },
                                        )*/
                                    }


                                }
                            )
                        },

                        bottomBar = {
                            BottomAppBar(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ) {

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "Shamiko白名单",
                                )

                                Switch(checked = isChecked, onCheckedChange = {isChecked = toggleWhitelist(this@MainActivity,it)})
                            }
                        },

                    ) { innerPadding ->

                        if (isShow){
                            AlertDialog(
                                icon = {
                                    Icon(
                                        Icons.Filled.Notifications,
                                        contentDescription = "Example Icon"
                                    )
                                },
                                title = {
                                    Text(text = "你确定想让“$mName”获得root吗？")
                                },

                                onDismissRequest = {
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            processExec("su $mUid -c su")
                                            isShow = !isShow
                                        }
                                    ) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = {
                                            isShow = !isShow
                                        }
                                    ) {
                                        Text("取消")
                                    }
                                }
                            )
                        }





                        LazyColumn(
                            modifier = Modifier.padding(innerPadding),
                            content = {


                                items(items = resolvedInfos,
                                    key = { it }) {
                                        applicationInfo ->

                                    val uid = applicationInfo.uid

                                    val appName = remember(pm) {
                                        applicationInfo.loadLabel(pm).toString()
                                    }

                                    val packageName = remember {
                                        applicationInfo.packageName
                                    }
                                    val iconDrawable = remember(pm) {
                                        applicationInfo.loadIcon(pm)
                                    }

                                    val bitmap = remember {
                                        iconDrawable.toBitmap(100,100).asImageBitmap()
                                    }

                                    val scope = rememberCoroutineScope()



                                    ConstraintLayout(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(top = 16.dp)
                                            .clickable {
                                                mUid = uid
                                                mName = appName

                                                isShow = !isShow

                                            }
                                    ) {
                                        val (icon,name,mPackageName) = createRefs()
                                        Image(
                                            bitmap = bitmap,
                                            modifier = Modifier
                                                .constrainAs(icon){
                                                    start.linkTo(parent.start,16.dp)
                                                    top.linkTo(parent.top)
                                                    bottom.linkTo(parent.bottom)
                                                    width = Dimension.value(36.dp)
                                                    height = Dimension.value(36.dp)

                                            },
                                            contentDescription = "")
                                        Text(
                                            text = appName,
                                            maxLines = 1,
                                            textAlign = TextAlign.Left,
                                            modifier = Modifier
                                                .constrainAs(name){
                                                    top.linkTo(parent.top,8.dp)
                                                    start.linkTo(icon.end,16.dp)
                                                    end.linkTo(parent.end,32.dp)
                                                    width = Dimension.fillToConstraints


                                            }
                                        )

                                        Text(
                                            text = packageName,
                                            maxLines = 1,
                                            textAlign = TextAlign.Left,
                                            color = Color.Unspecified.copy(0.5f),
                                            fontSize = 12.sp,
                                            modifier = Modifier
                                                .constrainAs(mPackageName){
                                                    top.linkTo(name.bottom,4.dp)
                                                    bottom.linkTo(parent.bottom,8.dp)
                                                    start.linkTo(icon.end,16.dp)
                                                    end.linkTo(parent.end,32.dp)
                                                    width = Dimension.fillToConstraints


                                                }
                                        )






                                    }







                                }
                            }
                        )
                    }






                }
            }
        }
    }

    fun search(string: String,isShowSystemApp:Boolean):List<ApplicationInfo>{
        val list:MutableList<ApplicationInfo> = emptyList<ApplicationInfo>().toMutableList()
        getAppList(isShowSystemApp).forEach {
            val name = it.loadLabel(pm).toString()

            if (Pattern.compile(string, Pattern.CASE_INSENSITIVE).matcher(name).find())
                list.add(it)

        }
        return list
    }

    fun getAppList(isShowSystemApp:Boolean):List<ApplicationInfo>{
        /*val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                mainIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            pm.queryIntentActivities(mainIntent, 0)
        }*/
        val list:MutableList<ApplicationInfo> = emptyList<ApplicationInfo>().toMutableList()

        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        packages.forEach {
            if (isShowSystemApp){
                list.add(it)

            }
            else{

                if (it.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    list.add(it)
                }




            }

        }

        return list
    }

    /**
     * 判断机器Android是否已经root，即是否获取root权限
     */
    open fun haveRoot() {
        // 通过执行测试命令来检测
        val ret = processExec("su")

    }


    open fun processExec(command: String?) {
        var ps: Process? = null
        try {
            ps = Runtime.getRuntime().exec(command+"\n")
            val pu = ProcessConfig(ps.errorStream)
            ps.outputStream.close()
            ps.waitFor()
            val errorStr = pu.errStr
            if (errorStr != null) {

                //log.error("执行解压操作异常！$errorStr")
            }
        } catch (e: IOException) {
            //log.error("执行解IO操作异常！$e")
        } catch (e: InterruptedException) {
            //log.error("执行解压打断异常！$e")
        } finally {
            ps?.destroy()
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    Text(
        text = "Hello $name!",
        modifier = modifier

    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MeowTheme {
        Greeting("Android")
    }
}