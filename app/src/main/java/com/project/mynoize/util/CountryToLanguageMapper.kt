package com.project.mynoize.util

fun Country.toLanguage(): Language = when (this) {
    Country.CROATIA                -> Language.CROATIAN
    Country.SERBIA                 -> Language.SERBIAN
    Country.BOSNIA_AND_HERZEGOVINA -> Language.BOSNIAN
    Country.UNITED_KINGDOM         -> Language.ENGLISH
    Country.UNITED_STATES          -> Language.ENGLISH
    Country.CANADA                 -> Language.ENGLISH
    Country.AUSTRALIA              -> Language.ENGLISH
    Country.IRELAND                -> Language.ENGLISH
    Country.MALTA                  -> Language.ENGLISH
    Country.ITALY                  -> Language.ITALIAN
    Country.AUSTRIA                -> Language.GERMAN
    Country.GERMANY                -> Language.GERMAN
    Country.LUXEMBOURG             -> Language.GERMAN
    Country.FRANCE                 -> Language.FRENCH
    Country.BELGIUM                -> Language.FRENCH
    Country.SPAIN                  -> Language.SPANISH
    Country.BRAZIL                 -> Language.PORTUGUESE
    Country.PORTUGAL               -> Language.PORTUGUESE
    Country.SOUTH_KOREA            -> Language.KOREAN
    Country.JAPAN                  -> Language.JAPANESE
    Country.CHINA                  -> Language.CHINESE
    Country.INDIA                  -> Language.HINDI
    Country.RUSSIA                 -> Language.RUSSIAN
    Country.NETHERLANDS            -> Language.DUTCH
    Country.SWEDEN                 -> Language.SWEDISH
    Country.POLAND                 -> Language.POLISH
    Country.DENMARK                -> Language.DANISH
    Country.FINLAND                -> Language.FINNISH
    Country.MACEDONIA,
    Country.BULGARIA,
    Country.CYPRUS,
    Country.CZECH_REPUBLIC,
    Country.ESTONIA,
    Country.GREECE,
    Country.HUNGARY,
    Country.LATVIA,
    Country.LITHUANIA,
    Country.ROMANIA,
    Country.SLOVAKIA,
    Country.SLOVENIA,
    Country.OTHER                  -> Language.OTHER
}