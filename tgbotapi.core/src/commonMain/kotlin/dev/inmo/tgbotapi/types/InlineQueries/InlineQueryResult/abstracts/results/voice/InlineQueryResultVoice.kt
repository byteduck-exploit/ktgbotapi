package dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.results.voice

import dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.DuratedInlineResultQuery
import dev.inmo.tgbotapi.types.InlineQueries.InlineQueryResult.abstracts.UrlInlineQueryResult

interface InlineQueryResultVoice : InlineQueryResultVoiceCommon, UrlInlineQueryResult, DuratedInlineResultQuery