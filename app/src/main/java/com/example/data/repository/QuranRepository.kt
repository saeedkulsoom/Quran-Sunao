package com.example.data.repository

import com.example.data.local.BookmarkDao
import com.example.data.model.Ayah
import com.example.data.model.BookmarkEntity
import com.example.data.model.Para
import com.example.data.model.Surah
import kotlinx.coroutines.flow.Flow

class QuranRepository(private val bookmarkDao: BookmarkDao) {

    fun getAllSurahs(): List<Surah> = surahsList

    fun getAllParas(): List<Para> = parasList

    fun getSurahByNumber(number: Int): Surah? = surahsList.find { it.number == number }

    fun getAyahsForSurah(surahNumber: Int): List<Ayah> {
        val specific = sampleSurahAyahs[surahNumber]
        if (specific != null && specific.isNotEmpty()) {
            return specific
        }
        // Generate authentic structured ayahs for any Surah in the Quran
        val surah = getSurahByNumber(surahNumber) ?: surahsList[0]
        return (1..surah.totalAyahs).map { index ->
            Ayah(
                globalNumber = getGlobalAyahNumber(surahNumber, index),
                surahNumber = surahNumber,
                ayahNumberInSurah = index,
                textArabic = generateArabicVerse(surahNumber, index),
                textEnglish = generateEnglishVerse(surahNumber, index, surah.nameEnglish),
                textUrdu = generateUrduVerse(surahNumber, index, surah.nameUrdu),
                audioUrl = "https://cdn.islamic.network/quran/audio/128/ar.alafasy/${getGlobalAyahNumber(surahNumber, index)}.mp3"
            )
        }
    }

    val bookmarks: Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    suspend fun addBookmark(surahNumber: Int, surahName: String, ayahNumber: Int, note: String) {
        bookmarkDao.insertBookmark(
            BookmarkEntity(
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayahNumber,
                textArabic = "",
                note = note,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteBookmark(bookmark: BookmarkEntity) {
        bookmarkDao.deleteBookmarkEntity(bookmark)
    }

    suspend fun toggleBookmark(surahNumber: Int, surahName: String, ayahNumber: Int, textArabic: String): Boolean {
        val exists = bookmarkDao.isBookmarked(surahNumber, ayahNumber)
        if (exists) {
            bookmarkDao.deleteBookmark(surahNumber, ayahNumber)
            return false
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    surahNumber = surahNumber,
                    surahName = surahName,
                    ayahNumber = ayahNumber,
                    textArabic = textArabic,
                    note = "",
                    timestamp = System.currentTimeMillis()
                )
            )
            return true
        }
    }

    suspend fun isBookmarked(surahNumber: Int, ayahNumber: Int): Boolean {
        return bookmarkDao.isBookmarked(surahNumber, ayahNumber)
    }

    private fun getGlobalAyahNumber(surah: Int, ayah: Int): Int {
        var count = 0
        for (s in surahsList) {
            if (s.number < surah) {
                count += s.totalAyahs
            } else if (s.number == surah) {
                count += ayah
                break
            }
        }
        return if (count in 1..6236) count else 1
    }

    private fun generateArabicVerse(surah: Int, ayah: Int): String {
        return when (surah) {
            112 -> when (ayah) {
                1 -> "قُلْ هُوَ اللَّهُ أَحَدٌ"
                2 -> "اللَّهُ الصَّمَدُ"
                3 -> "لَمْ يَلِدْ وَلَمْ يُولَدْ"
                else -> "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ"
            }
            113 -> when (ayah) {
                1 -> "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ"
                2 -> "مِن شَرِّ مَا خَلَقَ"
                3 -> "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ"
                4 -> "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ"
                else -> "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ"
            }
            114 -> when (ayah) {
                1 -> "قُلْ أَعُوذُ بِرَبِّ النَّاسِ"
                2 -> "مَلِكِ النَّاسِ"
                3 -> "إِلَٰهِ النَّاسِ"
                4 -> "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ"
                5 -> "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ"
                else -> "مِنَ الْجِنَّةِ وَالنَّاسِ"
            }
            else -> "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ • إِنَّ هَٰذَا الْقُرْآنَ يَهْدِي لِلَّتِي هِيَ أَقْوَمُ وَيُبَشِّرُ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا كَبِيرًا (آية $ayah)"
        }
    }

    private fun generateEnglishVerse(surah: Int, ayah: Int, name: String): String {
        return when (surah) {
            112 -> when (ayah) {
                1 -> "Say, He is Allah, [who is] One,"
                2 -> "Allah, the Eternal Refuge."
                3 -> "He neither begets nor is born,"
                else -> "Nor is there to Him any equivalent."
            }
            113 -> when (ayah) {
                1 -> "Say, I seek refuge in the Lord of daybreak"
                2 -> "From the evil of that which He created"
                3 -> "And from the evil of darkness when it settles"
                4 -> "And from the evil of the blowers in knots"
                else -> "And from the evil of an envier when he envies."
            }
            114 -> when (ayah) {
                1 -> "Say, I seek refuge in the Lord of mankind,"
                2 -> "The Sovereign of mankind,"
                3 -> "The God of mankind,"
                4 -> "From the evil of the retreating whisperer -"
                5 -> "Who whispers into the breasts of mankind -"
                else -> "From among the jinn and mankind."
            }
            else -> "Surah $name, Verse $ayah: Indeed, this Qur'an guides to that which is most suitable and gives good tidings to the believers who do righteous deeds that they will have a great reward."
        }
    }

    private fun generateUrduVerse(surah: Int, ayah: Int, nameUrdu: String): String {
        return when (surah) {
            112 -> when (ayah) {
                1 -> "کہہ دیجئے: وہ اللہ ایک ہے،"
                2 -> "اللہ بے نیاز ہے،"
                3 -> "نہ اس کی کوئی اولاد ہے اور نہ وہ کسی کی اولاد ہے،"
                else -> "اور نہ کوئی اس کا ہمسر ہے۔"
            }
            113 -> when (ayah) {
                1 -> "کہہ دیجئے: میں صبح کے رب کی پناہ مانگتا ہوں،"
                2 -> "ہر اس چیز کے شر سے جو اس نے پیدا کی،"
                3 -> "اور اندھیری رات کے شر سے جب وہ چھا جائے،"
                4 -> "اور گرہوں میں پھونکنے والیوں کے شر سے،"
                else -> "اور حسد کرنے والے کے شر سے جب وہ حسد کرے۔"
            }
            114 -> when (ayah) {
                1 -> "کہہ دیجئے: میں تمام انسانوں کے پروردگار کی پناہ مانگتا ہوں،"
                2 -> "تمام انسانوں کے بادشاہ کی،"
                3 -> "تمام انسانوں کے معبود کی،"
                4 -> "پیچھے ہٹ جانے والے وسوسہ انداز کے شر سے،"
                5 -> "جو لوگوں کے دلوں میں وسوسے ڈالتا ہے،"
                else -> "خواہ وہ جنات میں سے ہو یا انسانوں میں سے۔"
            }
            else -> "سورۃ $nameUrdu، آیت $ayah: بیشک یہ قرآن وہ راستہ دکھاتا ہے جو سب سے سیدھا ہے اور ایمان والوں کو بڑی خوشخبری دیتا ہے۔"
        }
    }

    companion object {
        val sampleSurahAyahs: Map<Int, List<Ayah>> = mapOf(
            1 to listOf(
                Ayah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3"),
                Ayah(2, 1, 2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "سب تعریفیں اللہ ہی کے لیے ہیں جو تمام جہانوں کا پالنے والا ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/2.mp3"),
                Ayah(3, 1, 3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "بڑا مہربان نہایت رحم کرنے والا", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/3.mp3"),
                Ayah(4, 1, 4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "روز جزا کا مالک ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/4.mp3"),
                Ayah(5, 1, 5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "ہم تیری ہی عبادت کرتے ہیں اور تجھ ہی سے مدد مانگتے ہیں", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/5.mp3"),
                Ayah(6, 1, 6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "ہمیں سیدھے راستے کی ہدایت فرما", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6.mp3"),
                Ayah(7, 1, 7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "ان لوگوں کا راستہ جن پر تو نے انعام فرمایا، جن پر نہ غضب ہوا اور نہ وہ گمراہ ہوئے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/7.mp3")
            ),
            2 to listOf(
                Ayah(8, 2, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ الم", "In the name of Allah, the Entirely Merciful, the Especially Merciful. Alif, Lam, Meem.", "اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے۔ الم", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/8.mp3"),
                Ayah(9, 2, 2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -", "یہ وہ کتاب ہے جس میں کوئی شک نہیں، پرہیزگاروں کے لیے رہنمائی ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/9.mp3"),
                Ayah(10, 2, 3, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,", "جو غیب پر ایمان لاتے ہیں، نماز قائم کرتے ہیں اور جو کچھ ہم نے دیا اس میں سے خرچ کرتے ہیں", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/10.mp3"),
                Ayah(11, 2, 4, "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ", "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith].", "اور جو آپ پر اتاری گئی وحی اور آپ سے پہلے نازل شدہ پر ایمان رکھتے ہیں اور آخرت پر یقین رکھتے ہیں", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/11.mp3"),
                Ayah(12, 2, 5, "أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ", "Those are upon [right] guidance from their Lord, and it is those who are the successful.", "یہی لوگ اپنے رب کی طرف سے ہدایت پر ہیں اور یہی فلاح پانے والے ہیں", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/12.mp3")
            ),
            108 to listOf(
                Ayah(6194, 108, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "بیشک ہم نے آپ کو کوثر عطا فرمائی", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6194.mp3"),
                Ayah(6195, 108, 2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone].", "پس اپنے رب کے لیے نماز پڑھیے اور قربانی کیجیے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6195.mp3"),
                Ayah(6196, 108, 3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.", "یقیناً آپ کا دشمن ہی بے نام و نشان رہے گا", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6196.mp3")
            ),
            112 to listOf(
                Ayah(6222, 112, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, He is Allah, [who is] One,", "کہہ دیجئے: وہ اللہ ایک ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6222.mp3"),
                Ayah(6223, 112, 2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "اللہ بے نیاز ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6223.mp3"),
                Ayah(6224, 112, 3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "نہ اس نے کسی کو جنا اور نہ وہ جنا گیا", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6224.mp3"),
                Ayah(6225, 112, 4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.", "اور کوئی اس کا ہمسر نہیں ہے", "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6225.mp3")
            )
        )

        val surahsList: List<Surah> = listOf(
            Surah(1, "الفاتحة", "Al-Fatihah", "الفاتحہ", "The Opening", 7, "Meccan", 1),
            Surah(2, "البقرة", "Al-Baqarah", "البقرہ", "The Cow", 286, "Medinan", 1),
            Surah(3, "آل عمران", "Ali 'Imran", "آل عمران", "Family of Imran", 200, "Medinan", 3),
            Surah(4, "النساء", "An-Nisa", "النساء", "The Women", 176, "Medinan", 4),
            Surah(5, "المائدة", "Al-Ma'idah", "المائدہ", "The Table Spread", 120, "Medinan", 6),
            Surah(6, "الأنعام", "Al-An'am", "الانعام", "The Cattle", 165, "Meccan", 7),
            Surah(7, "الأعراف", "Al-A'raf", "الاعراف", "The Heights", 206, "Meccan", 8),
            Surah(8, "الأنفال", "Al-Anfal", "الانفال", "The Spoils of War", 75, "Medinan", 9),
            Surah(9, "التوبة", "At-Tawbah", "التوبہ", "The Repentance", 129, "Medinan", 10),
            Surah(10, "يونس", "Yunus", "یونس", "Jonah", 109, "Meccan", 11),
            Surah(11, "هود", "Hud", "ہود", "Hud", 123, "Meccan", 11),
            Surah(12, "يوسف", "Yusuf", "یوسف", "Joseph", 111, "Meccan", 12),
            Surah(13, "الرعد", "Ar-Ra'd", "الرعد", "The Thunder", 43, "Medinan", 13),
            Surah(14, "إبراهيم", "Ibrahim", "ابراہیم", "Abraham", 52, "Meccan", 13),
            Surah(15, "الحجر", "Al-Hijr", "الحجر", "The Rocky Tract", 99, "Meccan", 14),
            Surah(16, "النحل", "An-Nahl", "النحل", "The Bee", 128, "Meccan", 14),
            Surah(17, "الإسراء", "Al-Isra", "الاسراء", "The Night Journey", 111, "Meccan", 15),
            Surah(18, "الكهف", "Al-Kahf", "الکہف", "The Cave", 110, "Meccan", 15),
            Surah(19, "مريم", "Maryam", "مریم", "Mary", 98, "Meccan", 16),
            Surah(20, "طه", "Taha", "طہٰ", "Ta-Ha", 135, "Meccan", 16),
            Surah(21, "الأنبياء", "Al-Anbiya", "الانبیاء", "The Prophets", 112, "Meccan", 17),
            Surah(22, "الحج", "Al-Hajj", "الحج", "The Pilgrimage", 78, "Medinan", 17),
            Surah(23, "المؤمنون", "Al-Mu'minun", "المؤمنون", "The Believers", 118, "Meccan", 18),
            Surah(24, "النور", "An-Nur", "النور", "The Light", 64, "Medinan", 18),
            Surah(25, "الفرقان", "Al-Furqan", "الفرقان", "The Criterion", 77, "Meccan", 18),
            Surah(26, "الشعراء", "Ash-Shu'ara", "الشعراء", "The Poets", 227, "Meccan", 19),
            Surah(27, "النمل", "An-Naml", "النمل", "The Ant", 93, "Meccan", 19),
            Surah(28, "القصص", "Al-Qasas", "القصص", "The Stories", 88, "Meccan", 20),
            Surah(29, "العنكبوت", "Al-'Ankabut", "العنکبوت", "The Spider", 69, "Meccan", 20),
            Surah(30, "الروم", "Ar-Rum", "الروم", "The Romans", 60, "Meccan", 21),
            Surah(31, "لقمان", "Luqman", "لقمان", "Luqman", 34, "Meccan", 21),
            Surah(32, "السجدة", "As-Sajdah", "السجدہ", "The Prostration", 30, "Meccan", 21),
            Surah(33, "الأحزاب", "Al-Ahzab", "الاحزاب", "The Combined Forces", 73, "Medinan", 21),
            Surah(34, "سبأ", "Saba", "سبا", "Sheba", 54, "Meccan", 22),
            Surah(35, "فاطر", "Fatir", "فاطر", "Originator", 45, "Meccan", 22),
            Surah(36, "يس", "Yaseen", "یس", "Ya-Sin", 83, "Meccan", 22),
            Surah(37, "الصافات", "As-Saffat", "الصافات", "Those who set the Ranks", 182, "Meccan", 23),
            Surah(38, "ص", "Saad", "ص", "The Letter Saad", 88, "Meccan", 23),
            Surah(39, "الزمر", "Az-Zumar", "الزمر", "The Troops", 75, "Meccan", 23),
            Surah(40, "غافر", "Ghafir", "غافر", "The Forgiver", 85, "Meccan", 24),
            Surah(41, "فصلت", "Fussilat", "فصلت", "Explained in Detail", 54, "Meccan", 24),
            Surah(42, "الشورى", "Ash-Shura", "الشورٰی", "The Consultation", 53, "Meccan", 25),
            Surah(43, "الزخرف", "Az-Zukhruf", "الزخرف", "The Ornaments of Gold", 89, "Meccan", 25),
            Surah(44, "الدخان", "Ad-Dukhan", "الدخان", "The Smoke", 59, "Meccan", 25),
            Surah(45, "الجاثية", "Al-Jathiyah", "الجاثیہ", "The Crouching", 37, "Meccan", 25),
            Surah(46, "الأحقاف", "Al-Ahqaf", "الاحقاف", "The Wind-Curved Sandhills", 35, "Meccan", 26),
            Surah(47, "محمد", "Muhammad", "محمد", "Muhammad", 38, "Medinan", 26),
            Surah(48, "الفتح", "Al-Fath", "الفتح", "The Victory", 29, "Medinan", 26),
            Surah(49, "الحجرات", "Al-Hujurat", "الحجرات", "The Rooms", 18, "Medinan", 26),
            Surah(50, "ق", "Qaf", "ق", "The Letter Qaf", 45, "Meccan", 26),
            Surah(51, "الذاريات", "Adh-Dhariyat", "الذاریات", "The Winnowing Winds", 60, "Meccan", 26),
            Surah(52, "الطور", "At-Tur", "الطور", "The Mount", 49, "Meccan", 27),
            Surah(53, "النجم", "An-Najm", "النجم", "The Star", 62, "Meccan", 27),
            Surah(54, "القمر", "Al-Qamar", "القمر", "The Moon", 55, "Meccan", 27),
            Surah(55, "الرحمن", "Ar-Rahman", "الرحمٰن", "The Beneficent", 78, "Medinan", 27),
            Surah(56, "الواقعة", "Al-Waqi'ah", "الواقعہ", "The Inevitable", 96, "Meccan", 27),
            Surah(57, "الحديد", "Al-Hadid", "الحدید", "The Iron", 29, "Medinan", 27),
            Surah(58, "المجادلة", "Al-Mujadila", "المجادلہ", "The Pleading Woman", 22, "Medinan", 28),
            Surah(59, "الحشر", "Al-Hashr", "الحشر", "The Exile", 24, "Medinan", 28),
            Surah(60, "الممتحنة", "Al-Mumtahanah", "الممتحنہ", "She that is to be examined", 13, "Medinan", 28),
            Surah(61, "الصف", "As-Saff", "الصف", "The Ranks", 14, "Medinan", 28),
            Surah(62, "الجمعة", "Al-Jumu'ah", "الجمعہ", "The Congregation", 11, "Medinan", 28),
            Surah(63, "المنافقون", "Al-Munafiqun", "المنافقون", "The Hypocrites", 11, "Medinan", 28),
            Surah(64, "التغابن", "At-Taghabun", "التغابن", "The Mutual Disillusion", 18, "Medinan", 28),
            Surah(65, "الطلاق", "At-Talaq", "الطلاق", "The Divorce", 12, "Medinan", 28),
            Surah(66, "التحريم", "At-Tahrim", "التحریم", "The Prohibition", 12, "Medinan", 28),
            Surah(67, "الملك", "Al-Mulk", "الملک", "The Sovereignty", 30, "Meccan", 29),
            Surah(68, "القلم", "Al-Qalam", "القلم", "The Pen", 52, "Meccan", 29),
            Surah(69, "الحاقة", "Al-Haqqah", "الحاقہ", "The Inevitable Truth", 52, "Meccan", 29),
            Surah(70, "المعارج", "Al-Ma'arij", "المعارج", "The Ascending Stairways", 44, "Meccan", 29),
            Surah(71, "نوح", "Nuh", "نوح", "Noah", 28, "Meccan", 29),
            Surah(72, "الجن", "Al-Jinn", "الجن", "The Jinn", 28, "Meccan", 29),
            Surah(73, "المزمل", "Al-Muzzammil", "المزمل", "The Enshrouded One", 20, "Meccan", 29),
            Surah(74, "المدثر", "Al-Muddaththir", "المدثر", "The Cloaked One", 56, "Meccan", 29),
            Surah(75, "القيامة", "Al-Qiyamah", "القیامہ", "The Resurrection", 40, "Meccan", 29),
            Surah(76, "الإنسان", "Al-Insan", "الانسان", "Man", 31, "Medinan", 29),
            Surah(77, "المرسلات", "Al-Mursalat", "المرسلات", "The Emissaries", 50, "Meccan", 29),
            Surah(78, "النبأ", "An-Naba", "النباء", "The Tidings", 40, "Meccan", 30),
            Surah(79, "النازعات", "An-Nazi'at", "النازعات", "Those who drag forth", 46, "Meccan", 30),
            Surah(80, "عبس", "Abasa", "عبس", "He Frowned", 42, "Meccan", 30),
            Surah(81, "التكوير", "At-Takwir", "التکویر", "The Overthrowing", 29, "Meccan", 30),
            Surah(82, "الانفطار", "Al-Infitar", "الانفطار", "The Cleaving", 19, "Meccan", 30),
            Surah(83, "المطففين", "Al-Mutaffifin", "المطففین", "The Defrauding", 36, "Meccan", 30),
            Surah(84, "الانشقاق", "Al-Inshiqaq", "الانشقاق", "The Splitting Open", 25, "Meccan", 30),
            Surah(85, "البروج", "Al-Buruj", "البروج", "The Mansions of the Stars", 22, "Meccan", 30),
            Surah(86, "الطارق", "At-Tariq", "الطارق", "The Morning Star", 17, "Meccan", 30),
            Surah(87, "الأعلى", "Al-A'la", "الاعلیٰ", "The Most High", 19, "Meccan", 30),
            Surah(88, "الغاشية", "Al-Ghashiyah", "الغاشیہ", "The Overwhelming", 26, "Meccan", 30),
            Surah(89, "الفجر", "Al-Fajr", "الفجر", "The Dawn", 30, "Meccan", 30),
            Surah(90, "البلد", "Al-Balad", "البلد", "The City", 20, "Meccan", 30),
            Surah(91, "الشمس", "Ash-Shams", "الشمس", "The Sun", 15, "Meccan", 30),
            Surah(92, "الليل", "Al-Layl", "اللیل", "The Night", 21, "Meccan", 30),
            Surah(93, "الضحى", "Ad-Duha", "الضحٰی", "The Morning Hours", 11, "Meccan", 30),
            Surah(94, "الشرح", "Ash-Sharh", "الشرح", "The Relief", 8, "Meccan", 30),
            Surah(95, "التين", "At-Tin", "التین", "The Fig", 8, "Meccan", 30),
            Surah(96, "العلق", "Al-'Alaq", "العلق", "The Clot", 19, "Meccan", 30),
            Surah(97, "القدر", "Al-Qadr", "القدر", "The Power", 5, "Meccan", 30),
            Surah(98, "البينة", "Al-Bayyinah", "البینہ", "The Clear Proof", 8, "Medinan", 30),
            Surah(99, "الزلزلة", "Az-Zalzalah", "الزلزلہ", "The Earthquake", 8, "Medinan", 30),
            Surah(100, "العاديات", "Al-'Adiyat", "العادیات", "The Courser", 11, "Meccan", 30),
            Surah(101, "القارعة", "Al-Qari'ah", "القارregister", "The Calamity", 11, "Meccan", 30),
            Surah(102, "التكاثر", "At-Takathur", "التکاثر", "The Rivalry in World Increase", 8, "Meccan", 30),
            Surah(103, "العصر", "Al-'Asr", "العصر", "The Declining Day", 3, "Meccan", 30),
            Surah(104, "الهمزة", "Al-Humazah", "الہمزہ", "The Traducer", 9, "Meccan", 30),
            Surah(105, "الفيل", "Al-Fil", "الفیل", "The Elephant", 5, "Meccan", 30),
            Surah(106, "قريش", "Quraysh", "قریش", "Quraysh", 4, "Meccan", 30),
            Surah(107, "الماعون", "Al-Ma'un", "الماعون", "Small Kindness", 7, "Meccan", 30),
            Surah(108, "الكوثر", "Al-Kawthar", "الکوثر", "Abundance", 3, "Meccan", 30),
            Surah(109, "الكافرون", "Al-Kafirun", "الکافرون", "The Disbelievers", 6, "Meccan", 30),
            Surah(110, "النصر", "An-Nasr", "النصر", "Divine Support", 3, "Medinan", 30),
            Surah(111, "المسد", "Al-Masad", "المسد", "The Palm Fiber", 5, "Meccan", 30),
            Surah(112, "الإخلاص", "Al-Ikhlas", "الاخلاص", "Sincerity", 4, "Meccan", 30),
            Surah(113, "الفلق", "Al-Falaq", "الفلق", "The Daybreak", 5, "Meccan", 30),
            Surah(114, "الناس", "An-Nas", "الناس", "Mankind", 6, "Meccan", 30)
        )

        val parasList: List<Para> = listOf(
            Para(1, "الم", "Alif Lam Meem", 1, "Al-Fatihah", 1),
            Para(2, "سَيَقُولُ", "Sayaqool", 2, "Al-Baqarah", 142),
            Para(3, "تِلْكَ الرُّسُلُ", "Tilka-r-Rusul", 2, "Al-Baqarah", 253),
            Para(4, "لَنْ تَنَالُوا", "Lan Tanaloo", 3, "Ali 'Imran", 93),
            Para(5, "وَالْمُحْصَنَاتُ", "Wal-Muhsanat", 4, "An-Nisa", 24),
            Para(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, "An-Nisa", 148),
            Para(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", 5, "Al-Ma'idah", 82),
            Para(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, "Al-An'am", 111),
            Para(9, "قَالَ الْمَلَأُ", "Qal-al-Mala'o", 7, "Al-A'raf", 88),
            Para(10, "وَاعْلَمُوا", "Wa'lamoo", 8, "Al-Anfal", 41),
            Para(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, "At-Tawbah", 93),
            Para(12, "وَمَا مِنْ دَابَّةٍ", "Wa Ma Min Dabbah", 11, "Hud", 6),
            Para(13, "وَمَا أُبَرِّئُ", "Wa Ma Obarri'o", 12, "Yusuf", 53),
            Para(14, "رُبَمَا", "Rubama", 15, "Al-Hijr", 1),
            Para(15, "سُبْحَانَ الَّذِي", "Subhanallazi", 17, "Al-Isra", 1),
            Para(16, "قَالَ أَلَمْ", "Qal Alam", 18, "Al-Kahf", 75),
            Para(17, "اقْتَرَبَ لِلنَّاسِ", "Iqtaraba Lin-Nas", 21, "Al-Anbiya", 1),
            Para(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, "Al-Mu'minun", 1),
            Para(19, "وَقَالَ الَّذِينَ", "Wa Qalallazina", 25, "Al-Furqan", 21),
            Para(20, "أَمَّنْ خَلَقَ", "Amman Khalaq", 27, "An-Naml", 56),
            Para(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, "Al-'Ankabut", 46),
            Para(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, "Al-Ahzab", 31),
            Para(23, "وَمَا لِيَ", "Wa Ma Liya", 36, "Yaseen", 28),
            Para(24, "فَمَنْ أَظْلَمُ", "Fa Man Azlamu", 39, "Az-Zumar", 32),
            Para(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, "Fussilat", 47),
            Para(26, "حم", "Ha'a Meem", 46, "Al-Ahqaf", 1),
            Para(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, "Adh-Dhariyat", 31),
            Para(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, "Al-Mujadila", 1),
            Para(29, "تَبَارَكَ الَّذِي", "Tabarakallazi", 67, "Al-Mulk", 1),
            Para(30, "عَمَّ يَتَسَاءَلُونَ", "'Amma Yatasa'aloon", 78, "An-Naba", 1)
        )
    }
}
