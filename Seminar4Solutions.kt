package com.example.seminar4

import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


// Q1: Padding background dan tashqarida
@Composable
fun Q1_PaddingOutsideBackground() {
    Text(
        text = "Hi",
        modifier = Modifier
            .padding(16.dp)
            .background(Color.Yellow)
    )
}

// Q2: Background to'liq maydonni egallaydi, padding ichkarida (avval background, keyin padding)
@Composable
fun Q2_BackgroundBeforePadding(modifier: Modifier = Modifier) {
    Text(
        text = "Hi",
        modifier = modifier
            .background(Color.Yellow)
            .padding(16.dp)
    )
}

// Q3: Clickable tartibi (katta vs kichik tap target)
@Composable
fun Q3_ClickableOrder(onIconClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onIconClick() }
            .padding(12.dp)
            .size(24.dp)
    )

    Box(
        modifier = Modifier
            .padding(12.dp)
            .clickable { onIconClick() }
            .size(24.dp)
    )
}

// Q4: To'liq kenglikdagi Button
@Composable
fun Q4_FullWidthButton(onContinueClick: () -> Unit) {
    Button(
        onClick = { onContinueClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Continue")
    }
}

// Q5: Aniq 12dp x 12dp Box
@Composable
fun Q5_FixedSquareBox() {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(Color.Green)
    )
}

// Q6: Horizontal (16dp) va Vertical (8dp) padding
@Composable
fun Q6_HorizontalVerticalPadding() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("Card content")
    }
}

// Q7: Yumaloq burchakli (12dp) rangli fon
@Composable
fun Q7_RoundedBackground() {
    Column(
        modifier = Modifier
            .background(
                color = Color(0xFFEADDFF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text("Rounded card")
    }
}

// Q8: 1dp primary border + 12dp rounded shape
@Composable
fun Q8_RoundedBorderAndBackground() {
    Column(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = Color(0xFFEADDFF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text("Bordered rounded card")
    }
}

// Q9: Offset (layoutga ta'sir qilmaydi, faqat chizish pozitsiyasini suradi)
@Composable
fun Q9_OffsetTooltip() {
    Box(
        modifier = Modifier.offset(x = 4.dp, y = 2.dp)
    ) {
        Text("Tooltip")
    }
}

// Q10: Row ichida 2:1 nisbatda weight
@Composable
fun Q10_WeightedRow(isEnabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Enable notifications",
            modifier = Modifier.weight(2f)
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = onEnabledChange,
            modifier = Modifier.weight(1f)
        )
    }
}

// Q11: Box ichida markazga joylashgan logo
@Composable
fun Q11_CenteredLogo(logoResId: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(logoResId),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

// Q12: Tashqi modifier qabul qiluvchi qayta ishlatiluvchi UserAvatar
@Composable
fun UserAvatar(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.LightGray)
    ) {
        Text(
            text = name.firstOrNull()?.toString() ?: "",
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun Q12_UserAvatarCaller() {
    UserAvatar(
        name = "Mukhammadamin",
        modifier = Modifier.padding(8.dp)
    )
}

// Q13: fillMaxSize() dan keyin wrapContentSize(Alignment.Center)
@Composable
fun Q13_WrapContentCentered() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Loading…",
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        )
    }
}

// Q14: 16:9 aspectRatio bilan rasm
@Composable
fun Q14_AspectRatioImage(photoResId: Int) {
    Image(
        painter = painterResource(photoResId),
        contentDescription = "Product photo",
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f),
        contentScale = ContentScale.Crop
    )
}

// Q15: Capstone — 56dp doira avatar, 2dp primary ring, ichida bosh harf
@Composable
fun Q15_CapstoneAvatar(initial: String = "M") {
    Box(
        modifier = Modifier
            .size(56.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(2.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initial, color = Color.White)
    }
}

// ==========================================
// PART B — COMPOSE MODIFIER PRACTICE (16–30)
// ==========================================

// Q16: Vertikal skroll — rememberScrollState() ishlatiladi
@Composable
fun Q16_VerticalScrollColumn() {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        repeat(30) { index ->
            Text("Setting row #$index", modifier = Modifier.padding(16.dp))
        }
    }
}

// Q17: Gorizontal skroll Row
@Composable
fun Q17_HorizontalScrollRow(categories: List<String>) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier.horizontalScroll(scrollState)
    ) {
        categories.forEach { category ->
            Text(
                text = category,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

// Q18: UI test uchun testTag (testda onNodeWithTag("submit_button") bilan topiladi)
@Composable
fun Q18_TestableSubmitButton(onSubmit: () -> Unit) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.testTag("submit_button")
    ) {
        Text("Submit")
    }
}

// Q19: Accessibility — dekorativ chiziqda null, mazmunli ikonkada aniq tavsif
@Composable
fun Q19_AccessibilityExamples() {
    // Dekorativ ajratuvchi: contentDescription = null
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.Gray)
    )
    // Ma'noli ikona: screen reader uchun aniq matn
    Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = "Warning: location permission required"
    )
}

// Q20: graphicsLayer { alpha = ... } — recomposition siz alpha animatsiyasi
@Composable
fun Q20_GraphicsLayerAlpha(isVisible: Boolean) {
    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        label = "cardAlpha"
    )
    Card(
        modifier = Modifier.graphicsLayer {
            alpha = animatedAlpha
        }
    ) {
        Text("Fading card", modifier = Modifier.padding(16.dp))
    }
}

// Q21: Bitta graphicsLayer ichida rotation va scale
@Composable
fun Q21_CombinedGraphicsLayer(angle: Float, scaleFactor: Float) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                rotationZ = angle
                scaleX = scaleFactor
                scaleY = scaleFactor
            }
            .background(MaterialTheme.colorScheme.primary)
    )
}

// Q22: zIndex orqali sudralayotgan 1-kartani 2-kartadan yuqoriga chiqarish
@Composable
fun Q22_ZIndexDrag(isBeingDragged: Boolean) {
    Box {
        Card(
            modifier = Modifier.zIndex(if (isBeingDragged) 1f else 0f)
        ) {
            Text("First card", modifier = Modifier.padding(16.dp))
        }
        Card(
            modifier = Modifier.zIndex(0f)
        ) {
            Text("Second card", modifier = Modifier.padding(16.dp))
        }
    }
}

// Q23: Qayta ishlatiluvchi Modifier.dashedBorder
fun Modifier.dashedBorder(color: Color, strokeWidth: Dp): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
    )
    drawRoundRect(
        color = color,
        style = stroke
    )
}

// Q24: combinedClickable — oddiy bosish va uzoq bosish
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Q24_MarkerPin(onSelect: () -> Unit, onContextMenu: () -> Unit) {
    Icon(
        imageVector = Icons.Default.LocationOn,
        contentDescription = "Map marker",
        modifier = Modifier
            .size(40.dp)
            .combinedClickable(
                onClick = onSelect,
                onLongClick = onContextMenu
            )
    )
}

// Q25: requiredSize(100.dp) ota cheklov (masalan 60dp) dan qat'iy 100dp ushlab turadi
@Composable
fun Q25_RequiredSizeVsSize() {
    Box(modifier = Modifier.size(60.dp)) {
        // requiredSize 100dp bo'lib chiqadi (ota 60dp bo'lsa ham)
        Box(
            modifier = Modifier
                .requiredSize(100.dp)
                .background(Color.Red.copy(alpha = 0.5f))
        )
    }
}

// Q26: LazyColumn da contentPadding ishlatish
@Composable
fun Q26_LazyColumnContentPadding(messages: List<String>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp)
    ) {
        items(messages.size) { index ->
            Text(text = messages[index], modifier = Modifier.padding(vertical = 4.dp))
        }
    }
}

// Q27: BoxScope ichida matchParentSize()
@Composable
fun Q27_MatchParentSizeGlow(avatarResId: Int) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Cyan.copy(alpha = 0.35f), CircleShape)
        )
        Image(
            painter = painterResource(avatarResId),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
        )
    }
}

// Q28: RowScope extension sifatida yordamchi funksiya
@Composable
fun RowScope.Q28_MyRowItems() {
    Text("A", Modifier.weight(1f))
    Text("B", Modifier.weight(1f))
}

// Q29: 80% kenglik va gorizontal markazlashgan TextField
@Composable
fun Q29_CenteredSearchBar(query: String, onQueryChange: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

// Q30: clip() background() dan OLDIN kelishi kerak (yoki background(..., shape = ...))
@Composable
fun Q30_ClipBeforeBackground() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Cyan)
    )
}


// Q31–Q34 nazariy javoblar izoh shaklida:
// Q31: Material Design — bu faqat tashqi ko'rinish emas, balki Color, Typography, Shape, Elevation/Motion
//      kabi o'lchovlardan tashkil topgan tizim. U accessibility, izchillik va dizayn-kod uyg'unligini beradi.
// Q32: Dynamic Color (Material You) foydalanuvchi fon rasmidan (wallpaper) rang palitrasini yaratadi.
//      Android 12 (API 31 / Android S) dan boshlab qo'llab-quvvatlanadi; eski versiyalarda fallback ranglarga qaytadi.
// Q33: Material 2 da cheklangan qo'lda tanlangan rang rollari bo'lgan; Material 3 esa tonal palitralar asosida
//      ko'plab rollarni (Primary/Secondary/Tertiary Container, OnContainer, Fixed, Surface variantlar) avtomatik hosil qiladi.
// Q34: Material 2 da Shape o'lchamlari (Small, Medium, Large) kamroq edi; Material 3 da kengaytirilgan shkala
//      (ExtraSmall, Small, Medium, Large, ExtraLarge, Full) mavjud bo'lib, komponentlar shunga ko'ra qayta tasniflangan.

private val LightColors = lightColorScheme(primary = Color(0xFF6750A4))
private val DarkColors = darkColorScheme(primary = Color(0xFFD0BCFF))

// Q35: AppTheme (API 31+ dynamic color tekshiruvi)
@Composable
fun Q35_AppTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// Q36: Material 3 tugmalar ierarxiyasi (bitta asosiy Button, qolganlari Tonal va Outlined)
@Composable
fun Q36_ActionButtons(onSave: () -> Unit, onPreview: () -> Unit, onCancel: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onSave) { Text("Save") }
        FilledTonalButton(onClick = onPreview) { Text("Preview") }
        OutlinedButton(onClick = onCancel) { Text("Cancel") }
    }
}

// Q37: WindowSizeClass breakpointlari:
// - Compact: < 600dp (telefonlar portret holatda)
// - Medium: 600dp .. < 840dp (planshetlar portret yoki katta telefonlar landscape)
// - Expanded: >= 840dp (planshetlar landscape, desktop/katta ekranlar)

// Q38: windowSizeClass.widthSizeClass bo'yicha adaptiv navigatsiya
@Composable
fun Q38_AdaptiveNavigation(windowSizeClass: WindowSizeClass, content: @Composable () -> Unit) {
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            Column {
                Box(modifier = Modifier.weight(1f)) { content() }
                NavigationBar {}
            }
        }
        WindowWidthSizeClass.Medium -> {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {}
                Box(modifier = Modifier.weight(1f)) { content() }
            }
        }
        WindowWidthSizeClass.Expanded -> {
            PermanentNavigationDrawer(
                drawerContent = { PermanentNavigationDrawerSheet {} }
            ) {
                content()
            }
        }
    }
}

// Q39: configuration.screenWidthDp > 600 ni qo'lda yozish standart breakpointlardan chetlashishga olib keladi,
//      faqat startda tekshirish esa buklanadigan (foldable) ekranlar ochilganda/yopilganda o'lcham o'zgarishini sezmйdi.

// Q40: Deklarativ UI — matnni qo'lda o'zgartirish o'rniga state o'zgarishiga bog'lash
@Composable
fun Q40_DeclarativeCounter() {
    var count by remember { mutableIntStateOf(0) }
    Button(onClick = { count++ }) {
        Text("$count")
    }
}

// Q41: remember { } recomposition paytida qiymatni xotirada saqlab qoladi;
//      faqat o'sha state ni o'qigan Text qayta kompozitsiya qilinadi (smart recomposition).

// Q42: State Hoisting — CounterScreen va stateless Counter
@Composable
fun Q42_CounterScreen() {
    var count by remember { mutableIntStateOf(0) }
    Counter(
        count = count,
        onIncrement = { count++ }
    )
}

@Composable
fun Counter(
    count: Int,
    onIncrement: () -> Unit
) {
    Button(onClick = onIncrement) {
        Text("Count: $count")
    }
}