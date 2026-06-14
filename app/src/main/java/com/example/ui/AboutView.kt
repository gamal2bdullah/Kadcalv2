package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun AboutView() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("about_view_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Title Section with Arabic localized label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "حول التطبيق",
                tint = CosmicOrange,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = "حول التطبيق · KADcal",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Text(
                    text = "حول نظام KADcal لتصميم محاكاة فترات الأحمال الكهروضوئية",
                    color = CosmicMute,
                    fontSize = 11.sp
                )
            }
        }

        // Section 1: Overview (نبذة عن التطبيق)
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "المزايا",
                        tint = CosmicAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "نبذة عن التطبيق KADcal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Right
                    )
                }
                Divider(color = CosmicBorder, thickness = 1.dp)
                Text(
                    text = "تطبيق KADcal هو منصة محاكاة وهندسة الأحمال الكهربائية المتقدمة لتصميم الأنظمة الهندسية الشمسية الكهروضوئية (Solar PV Systems) بدقة متناهية. يقوم التطبيق بتقدير الاحتياجات الهندسية للمشاريع وتوفير الأدوات اللازمة للمهندسين والمقاولين للموازنة التامة للأطوار (Phase Balancing) ومحاكاة فترات التشغيل لتجنب زيادة تيار التغذية وبدء التشغيل لضمان استقرار وتوافقية أحجام العاكسات والألواح والبطاريات بشكل سليم واحترافي ومتكامل.",
                    color = CosmicText,
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Section 2: Core Functions (وظائف التطبيق الرئيسية)
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "الوظائف",
                        tint = CosmicOrange,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "وظائف التطبيق الرئيسية",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Divider(color = CosmicBorder, thickness = 1.dp)

                val functions = listOf(
                    "إدارة وتصنيف الأحمال الكهربائية" to "إدخال وتصنيف مرن للأجهزة ومعدات المشاريع (إنارة، تكييف، مضخات، محركات إلخ) مع تحديد جهد ومزود الأطوار (1Ø / 3Ø).",
                    "محاكاة الفترات التشغيلية للمواسم" to "تقدير استهلاك الأحمال بشكل موسمي منفصل بين فترات الصيف والشتاء وبساعات عمل دقيقة لكل جهاز للحصول على معامل استهلاك يومي وسنوي صحيح.",
                    "محرك حساب وتصميم النظام الشمسي (Sizing Engine)" to "محرك ذكي يحسب بدقة متناهية المقاييس المثالية للألواح والبطاريات وسعة العاكس بناءً على مستويات بدء الطاقة والتحمل الآمن للأجهزة المستمرة واللحظية.",
                    "محسن وموازن طور المغذيات (Phase Optimizer)" to "برمجية ذكية تحلل وتوزع الحمل أحادي المخرج على مصفوفة الأطوار الثلاثة لتقليل معامل عدم الاتزان وحماية التوصيلات من خلل الفولتية والتيار المرتد.",
                    "مدونة مراجعة المطابقة والمواصفات (Validation)" to "التحقق البرمجي التلقائي من توافق التوصيلات الهندسية الحاكمة ومراجعة الجهد ومفاتيح الفحص والسلامة."
                )

                functions.forEach { (title, desc) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "صحيح",
                            tint = CosmicGreen,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Column {
                            Text(
                                text = title,
                                color = CosmicOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = desc,
                                color = CosmicText,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        // Section 3: Merits (مزايا ومميزات KADcal)
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "المزايا",
                        tint = CosmicAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "مفهوم التطبيق ومميزاته الفنية",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Divider(color = CosmicBorder, thickness = 1.dp)

                val advantages = listOf(
                    "دقة المعايير الهندسية" to "يستخدم العلاقات الفيزيائية والرياضية الدقيقة لحساب شدة تيار الحمل الكامل وتيارات البدء الصدمية لدوائر الأطوار الأحادية والثلاثية.",
                    "استقرار وتوازن الأحمال الكلي" to "يعالج عدم اتزان الأحمال بكفاءة برمجية عالية لتجنب مخاطر انقطاع أطوار الطاقة والأعطال في التركيبات المتقدمة.",
                    "مكتبة مسبقة متكاملة للأجهزة القياسية" to "يحتوي على قاعدة مرجعية غنية للأجهزة المنزلية والصناعية الكبرى لتسهيل النمذجة والحساب المباشر دون عناء التنقيب والبحث عن المواصفات.",
                    "تقارير هندسية احترافية فورية" to "توليد ملفات تفصيلية جاهزة للمراجعة لإنشاء مخططات الأبعاد والتحقق وتصميم النظام للمستثمرين وقسم الصيانة."
                )

                advantages.forEach { (title, desc) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "✦ $title",
                            color = CosmicGreenLight,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = desc,
                            color = CosmicText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            textAlign = TextAlign.Right,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Section 4: Operating Company Profile & Branding (الشركة المشغلة)
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicPanel),
            border = BorderStroke(1.dp, CosmicBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "الجهة والشركة المشغلة للتطبيق",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
                Divider(color = CosmicBorder, thickness = 1.dp)

                // High Contrast Premium Brand Card for Corporate Logos representation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(BorderStroke(1.dp, Color(0xFFE2E8F0)), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .testTag("kad_power_logo_container")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Iconic Brand Logo Image rendering
                        Image(
                            painter = painterResource(id = R.drawable.ic_kad_logo),
                            contentDescription = "KAD Iconic Representative Logo",
                            modifier = Modifier
                                .size(90.dp)
                                .aspectRatio(1f)
                        )

                        // Typography Wordmark Logo Image rendering
                        Image(
                            painter = painterResource(id = R.drawable.ic_kad_wordmark),
                            contentDescription = "KAD Typography Wordmark Logo",
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Official corporate text descriptions in Arabic and English
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "الخلية الضوئية للطاقة البديلة والمقاولات المحدودة",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "KAD Power & Contracting Co. Ltd",
                        color = CosmicGreenLight,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تلتزم شركة الخلية الضوئية للطاقة البديلة والمقاولات المحدودة بريادة الحلول الهندسية وتكامل الأنظمة الهجينة والشمسية، موفرة أعلى مقاييس جودة المحاكاة والتركيب للمشاريع التجارية والمنزلية لضمان كفاءة تشغيل مستدامة.",
                        color = CosmicMute,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
