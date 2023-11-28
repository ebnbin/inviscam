
import hct.Hct
import utils.StringUtils
import java.io.File
import kotlin.math.max

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        writeXml()
    }

    private enum class BaseHue(
        val value: Int,
        title: String,
    ) {
        PINK(0, "Pink"),
        RED(30, "Red"),
        ORANGE(60, "Orange"),
        AMBER(90, "Amber"),
        LIME(120, "Lime"),
        GREEN(150, "Green"),
        TEAL(180, "Teal"),
        CYAN(210, "Cyan"),
        LIGHT_BLUE(240, "Light Blue"),
        INDIGO(270, "Indigo"),
        DEEP_PURPLE(300, "Deep Purple"),
        PURPLE(330, "Purple"),
        ;

        val underlineName: String = title.replace(" ", "_").lowercase()

        val camelCaseName: String = title.replace(" ", "")

        fun writeBaseColor(sb: StringBuilder, chroma: Int, tone: Int) {
            val chromaName = when (chroma) {
                -1 -> "full"
                -2 -> "half"
                else -> chroma.toString()
            }
            val chromaDouble = when (chroma) {
                -1 -> maxChroma(value)
                -2 -> maxChroma(value) / 2.0
                else -> chroma.toDouble()
            }
            val hct = Hct.from(value.toDouble(), chromaDouble, tone.toDouble())
            sb.appendLine("    <color name=\"material_${underlineName}_${chromaName}_${tone}\">${StringUtils.hexFromArgb(hct.toInt())}</color>")
        }

        fun writeLightBaseTheme(sb: StringBuilder) {
            sb.appendLine("    <style name=\"MaterialTheme.${camelCaseName}.Base.Light\" parent=\"@style/MaterialTheme.Light\">")
            sb.appendLine("        <item name=\"colorOnPrimary\">@android:color/white</item>")
            sb.appendLine("        <item name=\"colorOutline\">@color/material_${underlineName}_8_50</item>")
            sb.appendLine("        <item name=\"android:colorBackground\">@color/material_${underlineName}_4_98</item>")
            sb.appendLine("        <item name=\"colorOnBackground\">@color/material_${underlineName}_4_10</item>")
            sb.appendLine("        <item name=\"colorSurfaceVariant\">@color/material_${underlineName}_8_90</item>")
            sb.appendLine("        <item name=\"colorOnSurfaceVariant\">@color/material_${underlineName}_8_30</item>")
            sb.appendLine("        <item name=\"colorSurfaceContainer\">@color/material_${underlineName}_4_94</item>")
            sb.appendLine("    </style>")
        }

        fun writeDarkBaseTheme(sb: StringBuilder) {
            sb.appendLine("    <style name=\"MaterialTheme.${camelCaseName}.Base.Dark\" parent=\"@style/MaterialTheme.Dark\">")
            sb.appendLine("        <item name=\"colorOutline\">@color/material_${underlineName}_8_60</item>")
            sb.appendLine("        <item name=\"android:colorBackground\">@color/material_${underlineName}_4_6</item>")
            sb.appendLine("        <item name=\"colorOnBackground\">@color/material_${underlineName}_4_90</item>")
            sb.appendLine("        <item name=\"colorSurfaceVariant\">@color/material_${underlineName}_8_30</item>")
            sb.appendLine("        <item name=\"colorOnSurfaceVariant\">@color/material_${underlineName}_8_80</item>")
            sb.appendLine("        <item name=\"colorSurfaceContainer\">@color/material_${underlineName}_4_12</item>")
            sb.appendLine("    </style>")
        }
    }

    private enum class Hue(
        val baseHue: BaseHue,
        isHalf: Boolean,
    ) {
        PINK_FULL(BaseHue.PINK, false),
        RED_FULL(BaseHue.RED, false),
        ORANGE_FULL(BaseHue.ORANGE, false),
        AMBER_FULL(BaseHue.AMBER, false),
        LIME_FULL(BaseHue.LIME, false),
        GREEN_FULL(BaseHue.GREEN, false),
        TEAL_FULL(BaseHue.TEAL, false),
        CYAN_FULL(BaseHue.CYAN, false),
        LIGHT_BLUE_FULL(BaseHue.LIGHT_BLUE, false),
        INDIGO_FULL(BaseHue.INDIGO, false),
        DEEP_PURPLE_FULL(BaseHue.DEEP_PURPLE, false),
        PURPLE_FULL(BaseHue.PURPLE, false),
        PINK_HALF(BaseHue.PINK, true),
        RED_HALF(BaseHue.RED, true),
        ORANGE_HALF(BaseHue.ORANGE, true),
        AMBER_HALF(BaseHue.AMBER, true),
        LIME_HALF(BaseHue.LIME, true),
        GREEN_HALF(BaseHue.GREEN, true),
        TEAL_HALF(BaseHue.TEAL, true),
        CYAN_HALF(BaseHue.CYAN, true),
        LIGHT_BLUE_HALF(BaseHue.LIGHT_BLUE, true),
        INDIGO_HALF(BaseHue.INDIGO, true),
        DEEP_PURPLE_HALF(BaseHue.DEEP_PURPLE, true),
        PURPLE_HALF(BaseHue.PURPLE, true),
        ;

        val fullHalfUnderlineName: String = if (isHalf) "half" else "full"

        val fullHalfCamelCaseName: String = if (isHalf) "Half" else "Full"

        fun writeAttr(sb: StringBuilder) {
            sb.appendLine("    <attr name=\"color${baseHue.camelCaseName}${fullHalfCamelCaseName}\" format=\"color\"/>")
        }

        fun writeColor(sb: StringBuilder, isNight: Boolean) {
            sb.appendLine("    <color name=\"material_${baseHue.underlineName}_${fullHalfUnderlineName}\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_${if (isNight) "70" else "50"}</color>")
        }

        fun writeLightTheme(sb: StringBuilder) {
            sb.appendLine("    <style name=\"MaterialTheme.${baseHue.camelCaseName}.${fullHalfCamelCaseName}.Light\" parent=\"@style/MaterialTheme.${baseHue.camelCaseName}.Base.Light\">")
            sb.appendLine("        <item name=\"colorPrimary\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_40</item>")
            sb.appendLine("        <item name=\"colorPrimaryContainer\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_90</item>")
            sb.appendLine("        <item name=\"colorOnPrimaryContainer\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_10</item>")
            sb.appendLine("    </style>")
        }

        fun writeDarkTheme(sb: StringBuilder) {
            sb.appendLine("    <style name=\"MaterialTheme.${baseHue.camelCaseName}.${fullHalfCamelCaseName}.Dark\" parent=\"@style/MaterialTheme.${baseHue.camelCaseName}.Base.Dark\">")
            sb.appendLine("        <item name=\"colorPrimary\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_80</item>")
            sb.appendLine("        <item name=\"colorOnPrimary\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_20</item>")
            sb.appendLine("        <item name=\"colorPrimaryContainer\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_30</item>")
            sb.appendLine("        <item name=\"colorOnPrimaryContainer\">@color/material_${baseHue.underlineName}_${fullHalfUnderlineName}_90</item>")
            sb.appendLine("    </style>")
        }

        fun writeDayNightTheme(sb: StringBuilder, isNight: Boolean) {
            sb.appendLine("    <style name=\"MaterialTheme.${baseHue.camelCaseName}.${fullHalfCamelCaseName}.DayNight\" parent=\"@style/MaterialTheme.${baseHue.camelCaseName}.${fullHalfCamelCaseName}.${if (isNight) "Dark" else "Light"}\"/>")
        }
    }

    private val CHROMA_TONE_LIST = listOf(
        4 to 6,
        4 to 10,
        4 to 12,
        4 to 90,
        4 to 94,
        4 to 98,
        8 to 30,
        8 to 50,
        8 to 60,
        8 to 80,
        8 to 90,
        -2 to 10,
        -2 to 20,
        -2 to 30,
        -2 to 40,
        -2 to 50,
        -2 to 70,
        -2 to 80,
        -2 to 90,
        -1 to 10,
        -1 to 20,
        -1 to 30,
        -1 to 40,
        -1 to 50,
        -1 to 70,
        -1 to 80,
        -1 to 90,
    )

    private fun maxChroma(hue: Int): Double {
        var maxChroma = 0.0
        IntRange(0, 100).forEach { tone ->
            val hct = Hct.from(hue.toDouble(), 120.0, tone.toDouble())
            maxChroma = max(maxChroma, hct.chroma)
        }
        return maxChroma
    }

    private fun writeXml(file: File, writer: (StringBuilder) -> Unit) {
        file.parentFile.mkdirs()
        val sb = StringBuilder()
        sb.appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
        sb.appendLine("<resources>")
        writer(sb)
        sb.appendLine("</resources>")
        file.writeText(sb.toString())
    }

    fun writeXml() {
        writeXml(File("libcore/src/main/res/values/material_theme.xml")) { sb ->
            // Attr.
            Hue.entries.forEach { hue ->
                hue.writeAttr(sb)
            }
            sb.appendLine()
            // Base color.
            BaseHue.entries.forEach { hue ->
                CHROMA_TONE_LIST.forEach { (chroma, tone) ->
                    hue.writeBaseColor(sb, chroma, tone)
                }
                sb.appendLine()
            }
            // Color.
            Hue.entries.forEach { fullHalfHue ->
                fullHalfHue.writeColor(sb, false)
                sb.appendLine()
            }
            // Base theme.
            sb.appendLine(BASE_THEME_TEXT)
            sb.appendLine()
            // LightDark base theme.
            BaseHue.entries.forEach { hue ->
                hue.writeLightBaseTheme(sb)
                sb.appendLine()
                hue.writeDarkBaseTheme(sb)
                sb.appendLine()
            }
            // LightDark theme.
            Hue.entries.forEach { fullHalfHue ->
                fullHalfHue.writeLightTheme(sb)
                sb.appendLine()
                fullHalfHue.writeDarkTheme(sb)
                sb.appendLine()
            }
            // DayNight theme.
            Hue.entries.forEach { fullHalfHue ->
                fullHalfHue.writeDayNightTheme(sb, false)
                sb.appendLine()
            }
            sb.setLength(sb.length - 1)
        }
        writeXml(File("libcore/src/main/res/values-v27/material_theme.xml")) { sb ->
            sb.appendLine(BASE_THEME_V27_TEXT)
        }
        writeXml(File("libcore/src/main/res/values-night/material_theme.xml")) { sb ->
            // Color.
            Hue.entries.forEach { fullHalfHue ->
                fullHalfHue.writeColor(sb, true)
                sb.appendLine()
            }
            // DayNight theme.
            Hue.entries.forEach { fullHalfHue ->
                fullHalfHue.writeDayNightTheme(sb, true)
                sb.appendLine()
            }
            sb.setLength(sb.length - 1)
        }
    }

    private val BASE_THEME_TEXT = """    <style name="MaterialTheme.Base.Light" parent="@style/Theme.Material3.Light.NoActionBar">
        <item name="colorSecondary">?attr/colorPrimary</item>
        <item name="colorOnSecondary">?attr/colorOnPrimary</item>
        <item name="colorSecondaryContainer">?attr/colorPrimaryContainer</item>
        <item name="colorOnSecondaryContainer">?attr/colorOnPrimaryContainer</item>
        <item name="colorTertiary">?attr/colorPrimary</item>
        <item name="colorOnTertiary">?attr/colorOnPrimary</item>
        <item name="colorTertiaryContainer">?attr/colorPrimaryContainer</item>
        <item name="colorOnTertiaryContainer">?attr/colorOnPrimaryContainer</item>
        <item name="colorSurface">?android:attr/colorBackground</item>
        <item name="colorOnSurface">?attr/colorOnBackground</item>
        <item name="android:statusBarColor">?android:attr/colorBackground</item>
        <item name="android:windowLightStatusBar">?attr/isLightTheme</item>
${Hue.entries.joinToString("\n") { hue ->
    "        <item name=\"color${hue.baseHue.camelCaseName}${hue.fullHalfCamelCaseName}\">@color/material_${hue.baseHue.underlineName}_${hue.fullHalfUnderlineName}_50</item>"
}}
    </style>

    <style name="MaterialTheme.Base.Dark" parent="@style/Theme.Material3.Dark.NoActionBar">
        <item name="colorSecondary">?attr/colorPrimary</item>
        <item name="colorOnSecondary">?attr/colorOnPrimary</item>
        <item name="colorSecondaryContainer">?attr/colorPrimaryContainer</item>
        <item name="colorOnSecondaryContainer">?attr/colorOnPrimaryContainer</item>
        <item name="colorTertiary">?attr/colorPrimary</item>
        <item name="colorOnTertiary">?attr/colorOnPrimary</item>
        <item name="colorTertiaryContainer">?attr/colorPrimaryContainer</item>
        <item name="colorOnTertiaryContainer">?attr/colorOnPrimaryContainer</item>
        <item name="colorSurface">?android:attr/colorBackground</item>
        <item name="colorOnSurface">?attr/colorOnBackground</item>
        <item name="android:statusBarColor">?android:attr/colorBackground</item>
        <item name="android:windowLightStatusBar">?attr/isLightTheme</item>
${Hue.entries.joinToString("\n") { hue ->
    "        <item name=\"color${hue.baseHue.camelCaseName}${hue.fullHalfCamelCaseName}\">@color/material_${hue.baseHue.underlineName}_${hue.fullHalfUnderlineName}_70</item>"
}}
    </style>

    <style name="MaterialTheme.Light" parent="@style/MaterialTheme.Base.Light"/>

    <style name="MaterialTheme.Dark" parent="@style/MaterialTheme.Base.Dark"/>"""

    private const val BASE_THEME_V27_TEXT = """    <style name="MaterialTheme.Light" parent="@style/MaterialTheme.Base.Light">
        <item name="android:navigationBarColor">?android:attr/colorBackground</item>
        <item name="android:windowLightNavigationBar">?attr/isLightTheme</item>
    </style>

    <style name="MaterialTheme.Dark" parent="@style/MaterialTheme.Base.Dark">
        <item name="android:navigationBarColor">?android:attr/colorBackground</item>
        <item name="android:windowLightNavigationBar">?attr/isLightTheme</item>
    </style>"""
}
