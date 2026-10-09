package com.example.data.repository

import com.example.data.model.CalendarSystem
import com.example.data.model.CountryInfo
import com.example.data.model.DateCertainty
import com.example.data.model.EventCategory
import com.example.data.model.TraditionType
import com.example.data.model.UniversalEvent

object GlobalEventDataset {

    val supportedCountries = listOf(
        CountryInfo(
            code = "US",
            name = "United States",
            nativeName = "United States",
            flagEmoji = "🇺🇸",
            region = "North America",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI, CalendarSystem.HEBREW),
            publicHolidayCount = 11,
            officialSources = "U.S. Office of Personnel Management (OPM)",
            description = "Diverse federal observances, state holidays, and cultural traditions celebrating freedom and democracy."
        ),
        CountryInfo(
            code = "JP",
            name = "Japan",
            nativeName = "日本",
            flagEmoji = "🇯🇵",
            region = "East Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.CHINESE_LUNISOLAR),
            publicHolidayCount = 16,
            officialSources = "Cabinet Office of Japan (Naikakufu)",
            description = "Rich integration of Shinto nature festivals, Buddhist ancestor remembrances, and national holidays like Golden Week."
        ),
        CountryInfo(
            code = "IN",
            name = "India",
            nativeName = "भारत",
            flagEmoji = "🇮🇳",
            region = "South Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HINDU_LUNISOLAR, CalendarSystem.HIJRI, CalendarSystem.BUDDHIST),
            publicHolidayCount = 18,
            officialSources = "Ministry of Personnel, Public Grievances and Pensions, Govt of India",
            description = "Uniquely multi-faith calendar observing national days, major Hindu festivals, Eid, Christmas, Guru Nanak Jayanti, and Mahavir Jayanti."
        ),
        CountryInfo(
            code = "PK",
            name = "Pakistan",
            nativeName = "پاکستان",
            flagEmoji = "🇵🇰",
            region = "South Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI),
            publicHolidayCount = 14,
            officialSources = "Ministry of Interior, Government of Pakistan",
            description = "Islamic lunar calendar observances regulated by the Central Ruet-e-Hilal Committee alongside historic national occasions."
        ),
        CountryInfo(
            code = "SA",
            name = "Saudi Arabia",
            nativeName = "المملكة العربية السعودية",
            flagEmoji = "🇸🇦",
            region = "Middle East",
            primaryCalendar = CalendarSystem.HIJRI,
            secondaryCalendars = listOf(CalendarSystem.GREGORIAN),
            publicHolidayCount = 5,
            officialSources = "Ministry of Human Resources and Social Development, KSA",
            description = "Home to Mecca and Medina; official civil affairs operate closely with the Umm al-Qura Hijri lunar calendar."
        ),
        CountryInfo(
            code = "GB",
            name = "United Kingdom",
            nativeName = "United Kingdom",
            flagEmoji = "🇬🇧",
            region = "Europe",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 8,
            officialSources = "GOV.UK Bank Holidays Schedule",
            description = "Historic Bank Holidays across England, Wales, Scotland, and Northern Ireland, with royal milestones and Celtic traditions."
        ),
        CountryInfo(
            code = "FR",
            name = "France",
            nativeName = "France",
            flagEmoji = "🇫🇷",
            region = "Europe",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 11,
            officialSources = "Service-Public.fr & Légifrance",
            description = "Celebrations of the French Republic (Fête Nationale), historical armistices, and historic Christian observances."
        ),
        CountryInfo(
            code = "DE",
            name = "Germany",
            nativeName = "Deutschland",
            flagEmoji = "🇩🇪",
            region = "Europe",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 10,
            officialSources = "Bundesministerium des Innern und für Heimat (BMI)",
            description = "Federal holidays such as German Unity Day combined with state-specific Bavarian and northern regional observances."
        ),
        CountryInfo(
            code = "CN",
            name = "China",
            nativeName = "中国",
            flagEmoji = "🇨🇳",
            region = "East Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.CHINESE_LUNISOLAR),
            publicHolidayCount = 11,
            officialSources = "State Council of the People's Republic of China",
            description = "Traditional Nongli (lunar agricultural calendar) festivals such as Spring Festival, Mid-Autumn Festival, and National Day Golden Week."
        ),
        CountryInfo(
            code = "BR",
            name = "Brazil",
            nativeName = "Brasil",
            flagEmoji = "🇧🇷",
            region = "South America",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 12,
            officialSources = "Ministério da Gestão e da Inovação em Serviços Públicos",
            description = "Vibrant blend of Carnaval, Tiradentes Day, Festa Junina, and national independence commemorations."
        ),
        CountryInfo(
            code = "MX",
            name = "Mexico",
            nativeName = "México",
            flagEmoji = "🇲🇽",
            region = "North America",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 9,
            officialSources = "Secretaría de Gobernación (SEGOB)",
            description = "Pre-Hispanic indigenous rituals intertwined with colonial history, such as Día de los Muertos and Grito de Dolores."
        ),
        CountryInfo(
            code = "EG",
            name = "Egypt",
            nativeName = "مصر",
            flagEmoji = "🇪🇬",
            region = "North Africa",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI),
            publicHolidayCount = 14,
            officialSources = "Egyptian Cabinet & Dar al-Ifta al-Misriyyah",
            description = "Observances spanning millennial heritage including Sham Ennessim (spring renewal), Islamic holidays, and Coptic Christmas."
        ),
        CountryInfo(
            code = "ZA",
            name = "South Africa",
            nativeName = "South Africa",
            flagEmoji = "🇿🇦",
            region = "Sub-Saharan Africa",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 12,
            officialSources = "South African Government Information Service",
            description = "Celebrations of human dignity and reconciliation, including Freedom Day, Youth Day, and National Heritage Day."
        ),
        CountryInfo(
            code = "NG",
            name = "Nigeria",
            nativeName = "Nigeria",
            flagEmoji = "🇳🇬",
            region = "West Africa",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI),
            publicHolidayCount = 11,
            officialSources = "Ministry of Interior, Federal Republic of Nigeria",
            description = "Equally honors major Islamic festivals, Christian holidays, and national Democracy Day across vibrant regional traditions."
        ),
        CountryInfo(
            code = "KR",
            name = "South Korea",
            nativeName = "대한민국",
            flagEmoji = "🇰🇷",
            region = "East Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.CHINESE_LUNISOLAR),
            publicHolidayCount = 15,
            officialSources = "Ministry of Personnel Management, Republic of Korea",
            description = "Major ancestral celebrations Seollal (Lunar New Year) and Chuseok (Harvest Festival) alongside Hangul Day and Independence Movement Day."
        ),
        CountryInfo(
            code = "CA",
            name = "Canada",
            nativeName = "Canada",
            flagEmoji = "🇨🇦",
            region = "North America",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 10,
            officialSources = "Government of Canada / Statutory Holidays",
            description = "Federal holidays, Canada Day, and the National Day for Truth and Reconciliation honoring Indigenous First Nations."
        ),
        CountryInfo(
            code = "AU",
            name = "Australia",
            nativeName = "Australia",
            flagEmoji = "🇦🇺",
            region = "Oceania",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 10,
            officialSources = "Fair Work Ombudsman & Australian Government",
            description = "Anzac Day, Australia Day, NAIDOC Indigenous week, and distinctive seasonal holidays in the Southern Hemisphere."
        ),
        CountryInfo(
            code = "TR",
            name = "Turkey",
            nativeName = "Türkiye",
            flagEmoji = "🇹🇷",
            region = "Eurasia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI),
            publicHolidayCount = 15,
            officialSources = "Presidency of the Republic of Turkey",
            description = "Rich bridge of East and West celebrating Republic Day, Victory Day, Children's Day, and national Ramadan/Sacrifice Eid holidays."
        ),
        CountryInfo(
            code = "ID",
            name = "Indonesia",
            nativeName = "Indonesia",
            flagEmoji = "🇮🇩",
            region = "Southeast Asia",
            primaryCalendar = CalendarSystem.GREGORIAN,
            secondaryCalendars = listOf(CalendarSystem.HIJRI, CalendarSystem.CHINESE_LUNISOLAR, CalendarSystem.HINDU_LUNISOLAR),
            publicHolidayCount = 16,
            officialSources = "Joint Ministerial Decree (SKB 3 Menteri), RI",
            description = "Diverse national holidays reflecting Pancasila: Eid, Christmas, Vesak, Nyepi (Balinese Day of Silence), and Chinese New Year."
        ),
        CountryInfo(
            code = "IT",
            name = "Italy",
            nativeName = "Italia",
            flagEmoji = "🇮🇹",
            region = "Europe",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 12,
            officialSources = "Governo Italiano - Presidenza del Consiglio",
            description = "Festa della Repubblica, Liberation Day, Ferragosto, and historic civic celebrations across Italian provinces."
        ),
        CountryInfo(
            code = "ES",
            name = "Spain",
            nativeName = "España",
            flagEmoji = "🇪🇸",
            region = "Europe",
            primaryCalendar = CalendarSystem.GREGORIAN,
            publicHolidayCount = 12,
            officialSources = "Boletín Oficial del Estado (BOE)",
            description = "Fiesta Nacional de España, Constitution Day, Dia de Reyes (Three Kings), and autonomous community festivals."
        )
    )
}
