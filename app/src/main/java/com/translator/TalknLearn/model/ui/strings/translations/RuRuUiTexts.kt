package com.translator.TalknLearn.model.ui

/**
 * Russian (ru-RU) UI text map — Тексты интерфейса на русском.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val RuRuUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Микрофон",
    UiTextKey.CopyButton to "Копировать",
    UiTextKey.SpeakScriptButton to "Озвучить оригинал",
    UiTextKey.TranslateButton to "Перевести",
    UiTextKey.CopyTranslationButton to "Копировать перевод",
    UiTextKey.SpeakTranslationButton to "Озвучить перевод",
    UiTextKey.RecognizingStatus to "Запись...говорите, остановится автоматически",
    UiTextKey.TranslatingStatus to "Перевод...",
    UiTextKey.SpeakingOriginalStatus to "Озвучивание оригинала...",
    UiTextKey.SpeakingTranslationStatus to "Озвучивание перевода...",
    UiTextKey.SpeakingLabel to "Воспроизведение",
    UiTextKey.FinishedSpeakingOriginal to "Озвучивание оригинала завершено",
    UiTextKey.FinishedSpeakingTranslation to "Озвучивание перевода завершено",
    UiTextKey.TtsErrorTemplate to "Ошибка голоса: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Язык приложения",
    UiTextKey.DetectLanguageLabel to "Определить язык",
    UiTextKey.TranslateToLabel to "Перевести на",

    // Language names
    UiTextKey.LangEnUs to "Английский",
    UiTextKey.LangZhHk to "Кантонский",
    UiTextKey.LangJaJp to "Японский",
    UiTextKey.LangZhCn to "Китайский (Упрощённый)",
    UiTextKey.LangFrFr to "Французский",
    UiTextKey.LangDeDe to "Немецкий",
    UiTextKey.LangKoKr to "Корейский",
    UiTextKey.LangEsEs to "Испанский",
    UiTextKey.LangIdId to "Индонезийский",
    UiTextKey.LangViVn to "Вьетнамский",
    UiTextKey.LangThTh to "Тайский",
    UiTextKey.LangFilPh to "Филиппинский",
    UiTextKey.LangMsMy to "Малайский",
    UiTextKey.LangPtBr to "Португальский",
    UiTextKey.LangItIt to "Итальянский",
    UiTextKey.LangRuRu to "Русский",

    // Navigation
    UiTextKey.NavHistory to "История",
    UiTextKey.NavLogin to "Войти",
    UiTextKey.NavLogout to "Выйти",
    UiTextKey.NavBack to "Назад",
    UiTextKey.ActionCancel to "Отмена",
    UiTextKey.ActionDelete to "Удалить",
    UiTextKey.ActionOpen to "Открыть",
    UiTextKey.ActionName to "Переименовать",
    UiTextKey.ActionSave to "Сохранить",
    UiTextKey.ActionConfirm to "Подтвердить",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Введите здесь или используйте микрофон...",
    UiTextKey.SpeechTranslatedPlaceholder to "Перевод появится здесь...",
    UiTextKey.StatusAzureErrorTemplate to "Ошибка Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Ошибка перевода: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Войдите для перевода",
    UiTextKey.StatusRecognizePreparing to "Подготовка микрофона...(не говорите пока)",
    UiTextKey.StatusRecognizeListening to "Слушаю...говорите",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Назад",
    UiTextKey.PaginationNextLabel to "Далее",
    UiTextKey.PaginationPageLabelTemplate to "Страница {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "Скопировано",
    UiTextKey.DisableText to "Войдите для перевода и сохранения истории",

    // Error
    UiTextKey.ErrorRetryButton to "Повторить",
    UiTextKey.ErrorGenericMessage to "Произошла ошибка. Повторите попытку",

    // Shop
    UiTextKey.ShopTitle to "Магазин",
    UiTextKey.ShopCoinBalance to "Мои монеты",
    UiTextKey.ShopHistoryExpansionTitle to "Расширение истории",
    UiTextKey.ShopHistoryExpansionDesc to "Увеличьте лимит истории для просмотра большего количества переводов",
    UiTextKey.ShopCurrentLimit to "Текущий лимит: {limit} записей",
    UiTextKey.ShopMaxLimit to "Максимальный лимит:",
    UiTextKey.ShopBuyHistoryExpansion to "Купить (+{increment} записей, {cost} монет)",
    UiTextKey.ShopInsufficientCoins to "Недостаточно монет",
    UiTextKey.ShopMaxLimitReached to "Достигнут максимальный лимит",
    UiTextKey.ShopHistoryExpandedTitle to "Успешно расширено!",
    UiTextKey.ShopHistoryExpandedMessage to "Ваша история теперь {limit} записей! Вы можете видеть больше переводов!",
    UiTextKey.ShopColorPaletteTitle to "Цветовые темы",
    UiTextKey.ShopColorPaletteDesc to "Выберите цветовую тему. 10 монет каждая",
    UiTextKey.ShopEntry to "Магазин",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Настройки голоса",
    UiTextKey.VoiceSettingsDesc to "Выберите голос для каждого языка",

    // Instructions
    UiTextKey.SpeechInstructions to "Нажмите микрофон для распознавания речи. Если автоопределение не обновляется после смены, нажмите обновить вверху справа",
    UiTextKey.HomeInstructions to "Выберите функцию для начала",
    UiTextKey.ContinuousInstructions to "Выберите два языка и начните разговор",

    // Home
    UiTextKey.HomeTitle to "Мгновенный перевод",
    UiTextKey.HelpTitle to "Справка",
    UiTextKey.SpeechTitle to "Быстрый перевод",
    UiTextKey.HomeStartButton to "Начать перевод",
    UiTextKey.HomeFeaturesTitle to "Функции",
    UiTextKey.HomeDiscreteDescription to "Перевод коротких текстов и аудио",
    UiTextKey.HomeContinuousDescription to "Двусторонний перевод в реальном времени",
    UiTextKey.HomeLearningDescription to "Создание материалов и тестов из истории",

    // Help
    UiTextKey.HelpCurrentTitle to "Текущие функции",
    UiTextKey.HelpCautionTitle to "Предупреждения",
    UiTextKey.HelpCurrentFeatures to "Текущие функции:\n" +
            "  • Быстрый перевод: распознавание и перевод речи\n" +
            "  • Непрерывный разговор: двусторонний голосовой перевод\n" +
            "  • История: просмотр прошлых переводов\n" +
            "  • Обучение: создание словаря и тестов\n\n" +
            "Перевод:\n" +
            "  • Распознавание речи Azure AI\n" +
            "  • Служба перевода Azure\n",
    UiTextKey.HelpCaution to "Предупреждения:\n" +
            "  • Распознавание речи требует интернет\n" +
            "  • Кэшированные переводы работают офлайн\n" +
            "  • Проверяйте важные переводы у специалистов\n\n" +
            "Аккаунт и данные:\n" +
            "  • Вход требуется для истории, обучения и монет\n" +
            "  • Данные безопасно хранятся в Firebase Firestore\n\n" +
            "Устранение неполадок:\n" +
            "  • При проблемах перезапустите приложение\n",
    UiTextKey.HelpNotesTitle to "Советы",
    UiTextKey.HelpNotes to "💡 Советы по использованию:\n\n" +
            "Для лучших переводов:\n" +
            "  • Говорите чётко в нормальном темпе\n" +
            "  • Уменьшите шум для лучшего распознавания\n" +
            "  • Быстрый перевод для коротких фраз\n\n" +
            "Язык приложения:\n" +
            "  • По умолчанию: английский, другие через ИИ\n" +
            "  • Ручной перевод для кантонского\n" +
            "Обновления и отзывы:\n" +
            "  • Версия приложения в Настройки → О приложении\n" +
            "  • Отправить отзыв в Настройки → Отзыв\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Отзыв",
    UiTextKey.FeedbackDesc to "Спасибо! Поделитесь предложениями, ошибками или оценкой",
    UiTextKey.FeedbackMessagePlaceholder to "Напишите отзыв...",
    UiTextKey.FeedbackSubmitButton to "Отправить",
    UiTextKey.FeedbackSubmitting to "Отправка...",
    UiTextKey.FeedbackSuccessTitle to "Спасибо!",
    UiTextKey.FeedbackSuccessMessage to "Отзыв отправлен. Спасибо!",
    UiTextKey.FeedbackErrorTitle to "Ошибка отправки",
    UiTextKey.FeedbackErrorMessage to "Не удалось отправить. Проверьте соединение и повторите",
    UiTextKey.FeedbackMessageRequired to "Текст отзыва обязателен",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Непрерывный разговор",
    UiTextKey.ContinuousStartButton to "Начать разговор",
    UiTextKey.ContinuousStopButton to "Остановить запись",
    UiTextKey.ContinuousStartScreenButton to "Непрерывный разговор",
    UiTextKey.ContinuousPersonALabel to "Говорящий A",
    UiTextKey.ContinuousPersonBLabel to "Говорящий B",
    UiTextKey.ContinuousCurrentStringLabel to "Текущий текст:",
    UiTextKey.ContinuousSpeakerAName to "Человек A",
    UiTextKey.ContinuousSpeakerBName to "Человек B",
    UiTextKey.ContinuousTranslationSuffix to " · Перевод",
    UiTextKey.ContinuousPreparingMicText to "Подготовка микрофона...(не говорите пока)",
    UiTextKey.ContinuousTranslatingText to "Перевод...",

    // History
    UiTextKey.HistoryTitle to "История",
    UiTextKey.HistoryTabDiscrete to "Быстрый перевод",
    UiTextKey.HistoryTabContinuous to "Непрерывный разговор",
    UiTextKey.HistoryNoContinuousSessions to "Нет сеансов разговора",
    UiTextKey.HistoryNoDiscreteRecords to "Переводов пока нет",
    UiTextKey.DialogDeleteRecordTitle to "Удалить запись?",
    UiTextKey.DialogDeleteRecordMessage to "Это действие нельзя отменить",
    UiTextKey.DialogDeleteSessionTitle to "Удалить сеанс?",
    UiTextKey.DialogDeleteSessionMessage to "Все записи в этом сеансе будут удалены. Это необратимо",
    UiTextKey.HistoryDeleteSessionButton to "Удалить",
    UiTextKey.HistoryNameSessionTitle to "Переименовать",
    UiTextKey.HistorySessionNameLabel to "Название сеанса",
    UiTextKey.HistorySessionTitleTemplate to "Сеанс {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} записей",

    // Filter
    UiTextKey.FilterDropdownDefault to "Все языки",
    UiTextKey.FilterTitle to "Фильтр истории",
    UiTextKey.FilterLangDrop to "Язык",
    UiTextKey.FilterKeyword to "Ключевое слово",
    UiTextKey.FilterApply to "Применить",
    UiTextKey.FilterCancel to "Отмена",
    UiTextKey.FilterClear to "Сбросить фильтр",
    UiTextKey.FilterHistoryScreenTitle to "Фильтр",

    // Auth
    UiTextKey.AuthLoginTitle to "Вход",
    UiTextKey.AuthRegisterTitle to "Регистрация (Приостановлена)",
    UiTextKey.AuthLoginHint to "Используйте зарегистрированный email и пароль",
    UiTextKey.AuthRegisterRules to "Регистрация приостановлена на время разработки\nВнимание: Пароль нельзя восстановить при неверном email\n" +
            "Правила регистрации:\n" +
            "• Действительный email (напр: имя@пример.com)\n" +
            "• Пароль от 8 символов\n" +
            "• Подтверждение пароля должно совпадать",
    UiTextKey.AuthEmailLabel to "Email",
    UiTextKey.AuthPasswordLabel to "Пароль",
    UiTextKey.AuthConfirmPasswordLabel to "Подтвердите пароль",
    UiTextKey.AuthLoginButton to "Войти",
    UiTextKey.AuthRegisterButton to "Зарегистрироваться",
    UiTextKey.AuthToggleToRegister to "Нет аккаунта? Зарегистрируйтесь (Приостановлено)",
    UiTextKey.AuthToggleToLogin to "Уже есть аккаунт? Войти",
    UiTextKey.AuthErrorPasswordsMismatch to "Пароли не совпадают",
    UiTextKey.AuthErrorPasswordTooShort to "Пароль должен быть не менее 8 символов",
    UiTextKey.AuthRegistrationDisabled to "Регистрация приостановлена на время разработки",
    UiTextKey.AuthResetEmailSent to "Письмо для сброса отправлено (если аккаунт существует). Проверьте почту",

    // Password reset
    UiTextKey.ForgotPwText to "Забыли пароль?",
    UiTextKey.ResetPwTitle to "Сброс пароля",
    UiTextKey.ResetPwText to "Введите email аккаунта. Мы отправим ссылку для сброса\nУбедитесь, что email зарегистрирован\n",
    UiTextKey.ResetSendingText to "Отправка...",
    UiTextKey.ResetSendText to "Отправить письмо для сброса",

    // Settings
    UiTextKey.SettingsTitle to "Настройки",
    UiTextKey.SettingsPrimaryLanguageTitle to "Основной язык",
    UiTextKey.SettingsPrimaryLanguageDesc to "Используется для описаний и материалов обучения",
    UiTextKey.SettingsPrimaryLanguageLabel to "Основной язык",
    UiTextKey.SettingsFontSizeTitle to "Размер шрифта",
    UiTextKey.SettingsFontSizeDesc to "Настройка размера шрифта (синхронизируется между устройствами)",
    UiTextKey.SettingsScaleTemplate to "Масштаб: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Цветовые темы",
    UiTextKey.SettingsColorPaletteDesc to "Выберите цветовую тему. 10 монет каждая",
    UiTextKey.SettingsColorCostTemplate to "{cost} монет",
    UiTextKey.SettingsColorUnlockButton to "Разблокировать",
    UiTextKey.SettingsColorSelectButton to "Выбрать",
    UiTextKey.SettingsColorAlreadyUnlocked to "Уже разблокировано",
    UiTextKey.SettingsPreviewHeadline to "Заголовок: Крупный текст",
    UiTextKey.SettingsPreviewBody to "Текст: Обычный текст",
    UiTextKey.SettingsPreviewLabel to "Метка: Мелкий текст",
    UiTextKey.SettingsAboutTitle to "О приложении",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Подключено. Настройки сохраняются и синхронизируются автоматически",
    UiTextKey.SettingsThemeTitle to "Тема",
    UiTextKey.SettingsThemeDesc to "Выберите тему: Системная, Светлая, Тёмная или По расписанию",
    UiTextKey.SettingsThemeSystem to "Системная",
    UiTextKey.SettingsThemeLight to "Светлая",
    UiTextKey.SettingsThemeDark to "Тёмная",
    UiTextKey.SettingsThemeScheduled to "По расписанию",
    UiTextKey.SettingsResetPW to "Сбросить пароль",
    UiTextKey.SettingsQuickLinks to "Другие настройки",
    UiTextKey.SettingsNotLoggedInWarning to "Войдите для доступа к настройкам аккаунта. Язык приложения можно менять без входа",
    UiTextKey.SettingsVoiceTitle to "Настройки голоса",
    UiTextKey.SettingsVoiceDesc to "Выберите голос для каждого языка",
    UiTextKey.SettingsVoiceLanguageLabel to "Язык",
    UiTextKey.SettingsVoiceSelectLabel to "Голос",
    UiTextKey.SettingsVoiceDefault to "По умолчанию",

    // Learning
    UiTextKey.LearningTitle to "Обучение",
    UiTextKey.LearningHintCount to "(*) Счётчик = переводы с участием этого языка",
    UiTextKey.LearningErrorTemplate to "Ошибка: %s",
    UiTextKey.LearningGenerate to "Создать",
    UiTextKey.LearningRegenerate to "Пересоздать",
    UiTextKey.LearningGenerating to "Создание...",
    UiTextKey.LearningOpenSheetTemplate to "Материал {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Учебный материал {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Основной язык: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Текущий счётчик: {nowCount} (при создании: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Нет содержимого",
    UiTextKey.LearningSheetRegenerate to "Пересоздать",
    UiTextKey.LearningSheetGenerating to "Создание...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 Что это?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Учебные материалы создаются из истории переводов. Включают словарь, определения, примеры и грамматику. Проверьте знания в тестах!",
    UiTextKey.LearningRegenBlockedTitle to "Невозможно пересоздать",
    UiTextKey.LearningRegenBlockedMessage to "Нужно минимум 5 дополнительных записей. Нужно ещё {needed}",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Нужно ещё {needed} записей для пересоздания (требуется 5 дополнительных)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ Счётчик должен быть выше, чем при последнем создании",
    UiTextKey.LearningRegenInfoTitle to "Правила пересоздания",
    UiTextKey.LearningRegenInfoMessage to "Пересоздание материалов:\n\n• Первый раз: в любой момент\n• Пересоздание: нужно 5 дополнительных записей\n\nКнопка станет зелёной, когда будет готово. Если серая, делайте больше переводов!\n\n💡 Совет: Если счётчик не обновляется, перезапустите приложение",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Тест уже создан для этой версии. Пересоздайте материалы для нового теста",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Тест: {language}",
    UiTextKey.QuizOpenButton to "📝 Тест",
    UiTextKey.QuizGenerateButton to "🔄 Создать тест",
    UiTextKey.QuizGenerating to "⏳ Создание...",
    UiTextKey.QuizUpToDate to "✓ Актуально",
    UiTextKey.QuizBlocked to "🚫 Заблокировано",
    UiTextKey.QuizWait to "⏳ Подождите...",
    UiTextKey.QuizMaterialsQuizTemplate to "Материал: {materials} | Тест: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Можно заработать монеты!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 Нужно ещё {count} для монет",
    UiTextKey.QuizCancelButton to "Отмена",
    UiTextKey.QuizPreviousButton to "Назад",
    UiTextKey.QuizNextButton to "Далее",
    UiTextKey.QuizSubmitButton to "Отправить",
    UiTextKey.QuizRetakeButton to "Пройти снова",
    UiTextKey.QuizBackButton to "Назад",
    UiTextKey.QuizLoadingText to "Загрузка теста...",
    UiTextKey.QuizGeneratingText to "Создание теста...",
    UiTextKey.QuizNoMaterialsTitle to "Материалы не найдены",
    UiTextKey.QuizNoMaterialsMessage to "Сначала создайте учебные материалы перед прохождением теста",
    UiTextKey.QuizErrorTitle to "⚠️ Ошибка теста",
    UiTextKey.QuizErrorSuggestion to "Совет: Используйте кнопку выше для создания теста",
    UiTextKey.QuizCompletedTitle to "Тест завершён!",
    UiTextKey.QuizAnswerReviewTitle to "Обзор ответов",
    UiTextKey.QuizYourAnswerTemplate to "Ваш ответ: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Правильный ответ: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Вопрос {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Невозможно пересоздать: материал({materials}) < тест({quiz}), делайте больше переводов",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Другое создание в процессе. Подождите",
    UiTextKey.QuizCoinRulesTitle to "🪙 Правила монет",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Как заработать:",
    UiTextKey.QuizCoinRulesRequirements to "Требования:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Текущий статус:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Можно заработать монеты в следующем тесте!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Нужно ещё {count} для монет",
    UiTextKey.QuizCoinRule1Coin to "• 1 монета за правильный ответ",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Только первая попытка засчитывается",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Тест должен соответствовать материалам",
    UiTextKey.QuizCoinRulePlus10 to "• Минимум 10 дополнительных записей с последней награды",
    UiTextKey.QuizCoinRuleNoDelete to "• Монеты не возвращаются при удалении записей",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Материал: {count} записей",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Тест: {count} записей",
    UiTextKey.QuizCoinRuleGotIt to "Понятно!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Пересоздать тест?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Можно заработать монеты в этом тесте! (только первая попытка)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ В этом тесте нельзя заработать монеты",
    UiTextKey.QuizRegenNeedMoreTemplate to "Нужно ещё {count} переводов для получения (минимум 10 с последней награды)",
    UiTextKey.QuizRegenReminder to "Совет: Можно тренироваться и повторять, но монеты только при первой попытке с достаточным количеством записей",
    UiTextKey.QuizRegenGenerateButton to "Создать",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Монеты заработаны!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Поздравляем! Вы заработали {Coins} монет!",
    UiTextKey.QuizCoinsRule1 to "• 1 монета за правильный ответ при первой попытке",
    UiTextKey.QuizCoinsRule2 to "• Нет монет при повторных попытках",
    UiTextKey.QuizCoinsRule3 to "• Новый тест требует ещё 10 записей",
    UiTextKey.QuizCoinsRule4 to "• Тест должен соответствовать материалам",
    UiTextKey.QuizCoinsRule5 to "• Монеты являются частью истории",
    UiTextKey.QuizCoinsGreatButton to "Отлично!",
    UiTextKey.QuizOutdatedMessage to "Этот тест использует устаревшие материалы",
    UiTextKey.QuizRecordsLabel to "Записи",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Мои монеты",
    UiTextKey.HistoryCoinRulesTitle to "Правила монет:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Как заработать:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 монета за правильный ответ",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Только первая попытка за версию",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Тест должен соответствовать текущему материалу",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Правила защиты:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Нужно 10 новых переводов с последней награды",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Версия теста должна совпадать с материалом",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Удаление записей блокирует пересоздание (кроме случаев, когда счётчик выше)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Нет монет при повторных попытках",
    UiTextKey.HistoryCoinTipsTitle to "💡 Советы:",
    UiTextKey.HistoryCoinTipsRule1 to "• Регулярно добавляйте переводы",
    UiTextKey.HistoryCoinTipsRule2 to "• Хорошо подготовьтесь к первой попытке!",
    UiTextKey.HistoryCoinGotItButton to "Понятно!",

    // History info
    UiTextKey.HistoryInfoTitle to "Информация об истории",
    UiTextKey.HistoryInfoLimitMessage to "История показывает {limit} последних записей. Расширьте в магазине!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Старые записи сохраняются, но скрыты для производительности",
    UiTextKey.HistoryInfoFavoritesMessage to "Хотите сохранить перевод? Нажмите ❤️ для добавления в избранное",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Просмотр сохранённых в Настройки → Избранное",
    UiTextKey.HistoryInfoFilterMessage to "Используйте фильтр для поиска среди {limit} отображаемых записей",
    UiTextKey.HistoryInfoGotItButton to "Понятно",

    // Word bank
    UiTextKey.WordBankTitle to "Словарь",
    UiTextKey.WordBankSelectLanguage to "Выберите язык для просмотра или создания словаря:",
    UiTextKey.WordBankNoHistory to "Нет истории переводов",
    UiTextKey.WordBankNoHistoryHint to "Начните переводить для создания словаря!",
    UiTextKey.WordBankWordsCount to "Слова",
    UiTextKey.WordBankGenerating to "Создание...",
    UiTextKey.WordBankGenerate to "Создать словарь",
    UiTextKey.WordBankRegenerate to "Пересоздать словарь",
    UiTextKey.WordBankRefresh to "🔄 Обновить словарь",
    UiTextKey.WordBankEmpty to "Словарь пуст",
    UiTextKey.WordBankEmptyHint to "Нажмите выше для создания из истории",
    UiTextKey.WordBankExample to "Пример:",
    UiTextKey.WordBankDifficulty to "Уровень:",
    UiTextKey.WordBankFilterCategory to "Категория",
    UiTextKey.WordBankFilterCategoryAll to "Все категории",
    UiTextKey.WordBankFilterDifficultyLabel to "Уровень:",
    UiTextKey.WordBankFilterNoResults to "Нет слов по фильтру",
    UiTextKey.WordBankRefreshAvailable to "✅ Доступно обновление!",
    UiTextKey.WordBankRecordsNeeded to "Записи (нужно 20 для обновления)",
    UiTextKey.WordBankRegenInfoTitle to "Правила обновления",
    UiTextKey.WordBankRegenInfoMessage to "Обновление словаря:\n\n• Первый раз: в любой момент\n• Обновление: нужно 20 дополнительных записей\n\nКнопка станет зелёной, когда будет готово. Если серая, делайте больше переводов!\n\n💡 Совет: Если счётчик не обновляется, перезапустите приложение",
    UiTextKey.WordBankHistoryCountTemplate to "Текущий счётчик: {nowCount} (при создании: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Выйти?",
    UiTextKey.DialogLogoutMessage to "Потребуется повторный вход для перевода и просмотра истории",
    UiTextKey.DialogGenerateOverwriteTitle to "Заменить материал?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "Текущий материал будет заменён\nСоздать материал для {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Профиль",
    UiTextKey.ProfileUsernameLabel to "Имя пользователя",
    UiTextKey.ProfileUsernameHint to "Введите имя пользователя",
    UiTextKey.ProfileUpdateButton to "Обновить профиль",
    UiTextKey.ProfileUpdateSuccess to "Профиль обновлён",
    UiTextKey.ProfileUpdateError to "Ошибка обновления",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Удалить аккаунт",
    UiTextKey.AccountDeleteWarning to "⚠️ Необратимое действие!",
    UiTextKey.AccountDeleteConfirmMessage to "Все данные будут удалены навсегда: история, словарь, материалы, настройки. Введите пароль для подтверждения",
    UiTextKey.AccountDeletePasswordLabel to "Пароль",
    UiTextKey.AccountDeleteButton to "Удалить аккаунт",
    UiTextKey.AccountDeleteSuccess to "Аккаунт успешно удалён",
    UiTextKey.AccountDeleteError to "Ошибка удаления",
    UiTextKey.AccountDeleteReauthRequired to "Введите пароль для подтверждения удаления",

    // Favorites
    UiTextKey.FavoritesTitle to "Избранное",
    UiTextKey.FavoritesEmpty to "Избранного пока нет",
    UiTextKey.FavoritesAddSuccess to "Добавлено в избранное",
    UiTextKey.FavoritesRemoveSuccess to "Удалено из избранного",
    UiTextKey.FavoritesAddButton to "В избранное",
    UiTextKey.FavoritesRemoveButton to "Из избранного",
    UiTextKey.FavoritesNoteLabel to "Заметка",
    UiTextKey.FavoritesNoteHint to "Добавить заметку (необязательно)",
    UiTextKey.FavoritesTabRecords to "Записи",
    UiTextKey.FavoritesTabSessions to "Сеансы",
    UiTextKey.FavoritesSessionsEmpty to "Нет избранных сеансов",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} сообщений",

    // Custom words
    UiTextKey.CustomWordsTitle to "Свои слова",
    UiTextKey.CustomWordsAdd to "Добавить слово",
    UiTextKey.CustomWordsEdit to "Редактировать слово",
    UiTextKey.CustomWordsDelete to "Удалить слово",
    UiTextKey.CustomWordsOriginalLabel to "Оригинал",
    UiTextKey.CustomWordsTranslatedLabel to "Перевод",
    UiTextKey.CustomWordsPronunciationLabel to "Произношение (необязательно)",
    UiTextKey.CustomWordsExampleLabel to "Пример (необязательно)",
    UiTextKey.CustomWordsSaveSuccess to "Слово сохранено",
    UiTextKey.CustomWordsDeleteSuccess to "Слово удалено",
    UiTextKey.CustomWordsAlreadyExists to "Слово уже существует",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Язык оригинала",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Язык перевода",
    UiTextKey.CustomWordsSaveButton to "Сохранить",
    UiTextKey.CustomWordsCancelButton to "Отмена",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Автоопределение",
    UiTextKey.LanguageDetectDetecting to "Определение...",
    UiTextKey.LanguageDetectedTemplate to "Определено: {language}",
    UiTextKey.LanguageDetectFailed to "Не удалось определить",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Сканировать текст с изображения",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Внимание: Распознавание может быть неточным. Проверьте текст. " +
            "Поддержка: Латиница (Английский и др.), Китайский, Японский и Корейский",
    UiTextKey.ImageRecognitionScanning to "Сканирование текста...",
    UiTextKey.ImageRecognitionSuccess to "Текст успешно распознан",

    // Cache
    UiTextKey.CacheClearButton to "Очистить кэш",
    UiTextKey.CacheClearSuccess to "Кэш очищен",
    UiTextKey.CacheStatsTemplate to "Кэш: {count} сохранённых переводов",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Автоматическая тема",
    UiTextKey.SettingsAutoThemeDesc to "Автоматическое переключение между светлой и тёмной темой по расписанию",
    UiTextKey.SettingsAutoThemeEnabled to "Включено",
    UiTextKey.SettingsAutoThemeDisabled to "Выключено",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Начало тёмной:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Начало светлой:",
    UiTextKey.SettingsAutoThemePreview to "Тема будет автоматически меняться по установленному расписанию",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Офлайн-режим",
    UiTextKey.OfflineModeMessage to "Вы офлайн. Будут показаны кэшированные данные",
    UiTextKey.OfflineModeRetry to "Повторить подключение",
    UiTextKey.OfflineDataCached to "Кэшированные данные доступны",
    UiTextKey.OfflineSyncPending to "Изменения будут синхронизированы при подключении",

    // Image capture
    UiTextKey.ImageSourceTitle to "Выберите источник изображения",
    UiTextKey.ImageSourceCamera to "Сделать фото",
    UiTextKey.ImageSourceGallery to "Выбрать из галереи",
    UiTextKey.ImageSourceCancel to "Отмена",
    UiTextKey.CameraCaptureContentDesc to "Сделать фото",

    // Friends
    UiTextKey.FriendsTitle to "Друзья",
    UiTextKey.FriendsMenuButton to "Друзья",
    UiTextKey.FriendsAddButton to "Добавить друга",
    UiTextKey.FriendsSearchTitle to "Поиск пользователя",
    UiTextKey.FriendsSearchPlaceholder to "Имя пользователя или ID...",
    UiTextKey.FriendsSearchMinChars to "Введите минимум 2 символа",
    UiTextKey.FriendsSearchNoResults to "Пользователь не найден",
    UiTextKey.FriendsListEmpty to "Добавьте друзей для чата и обмена материалами",
    UiTextKey.FriendsRequestsSection to "Запросы ({count})",
    UiTextKey.FriendsSectionTitle to "Друзья ({count})",
    UiTextKey.FriendsAcceptButton to "Принять",
    UiTextKey.FriendsRejectButton to "Отклонить",
    UiTextKey.FriendsRemoveButton to "Удалить",
    UiTextKey.FriendsRemoveDialogTitle to "Удалить друга",
    UiTextKey.FriendsRemoveDialogMessage to "Удалить {username} из списка друзей?",
    UiTextKey.FriendsSendRequestButton to "Добавить",
    UiTextKey.FriendsRequestSentSuccess to "Запрос отправлен!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Запрос принят!",
    UiTextKey.FriendsRequestRejectedSuccess to "Запрос отклонён",
    UiTextKey.FriendsRemovedSuccess to "Друг удалён",
    UiTextKey.FriendsRequestFailed to "Ошибка отправки",
    UiTextKey.FriendsCloseButton to "Закрыть",
    UiTextKey.FriendsCancelButton to "Отмена",
    UiTextKey.FriendsRemoveConfirm to "Удалить",
    UiTextKey.FriendsNewRequestsTemplate to "{count} новых запросов!",
    UiTextKey.FriendsSentRequestsSection to "Отправленные ({count})",
    UiTextKey.FriendsPendingStatus to "Ожидание",
    UiTextKey.FriendsCancelRequestButton to "Отменить запрос",
    UiTextKey.FriendsUnreadMessageDesc to "Отправить сообщение",
    UiTextKey.FriendsDeleteModeButton to "Удаление друзей",
    UiTextKey.FriendsDeleteSelectedButton to "Удалить выбранных",
    UiTextKey.FriendsDeleteMultipleTitle to "Удалить друзей",
    UiTextKey.FriendsDeleteMultipleMessage to "Удалить {count} выбранных друзей?",
    UiTextKey.FriendsSearchMinChars3 to "Введите минимум 3 символа для имени",
    UiTextKey.FriendsSearchByUserIdHint to "Или найдите по точному User ID",
    UiTextKey.FriendsStatusAlreadyFriends to "Уже друзья",
    UiTextKey.FriendsStatusRequestSent to "Отправлено — ожидание ответа",
    UiTextKey.FriendsStatusRequestReceived to "Этот пользователь отправил вам запрос",

    // Chat
    UiTextKey.ChatTitle to "Чат с {username}",
    UiTextKey.ChatInputPlaceholder to "Введите сообщение...",
    UiTextKey.ChatSendButton to "Отправить",
    UiTextKey.ChatEmpty to "Сообщений пока нет. Начните разговор!",
    UiTextKey.ChatMessageSent to "Сообщение отправлено",
    UiTextKey.ChatMessageFailed to "Ошибка отправки",
    UiTextKey.ChatMarkingRead to "Чтение...",
    UiTextKey.ChatLoadingMessages to "Загрузка сообщений...",
    UiTextKey.ChatToday to "Сегодня",
    UiTextKey.ChatYesterday to "Вчера",
    UiTextKey.ChatUnreadBadge to "{count} непрочитанных",
    UiTextKey.ChatTranslateButton to "Перевести",
    UiTextKey.ChatTranslateDialogTitle to "Перевести чат",
    UiTextKey.ChatTranslateDialogMessage to "Перевести сообщения друга на ваш язык? Каждое сообщение будет определено и переведено автоматически",
    UiTextKey.ChatTranslateConfirm to "Перевести всё",
    UiTextKey.ChatTranslating to "Перевод сообщений...",
    UiTextKey.ChatTranslated to "Сообщения переведены",
    UiTextKey.ChatShowOriginal to "Показать оригинал",
    UiTextKey.ChatShowTranslation to "Показать перевод",
    UiTextKey.ChatTranslateFailed to "Ошибка перевода",
    UiTextKey.ChatTranslatedLabel to "Переведено",

    // Sharing
    UiTextKey.ShareTitle to "Поделиться",
    UiTextKey.ShareInboxTitle to "Входящие",
    UiTextKey.ShareInboxEmpty to "Пока пусто. Друзья могут делиться словами и материалами!",
    UiTextKey.ShareWordButton to "Поделиться словом",
    UiTextKey.ShareMaterialButton to "Поделиться материалом",
    UiTextKey.ShareSelectFriendTitle to "Выберите друга",
    UiTextKey.ShareSelectFriendMessage to "Выберите друга для отправки:",
    UiTextKey.ShareSuccess to "Отправлено успешно!",
    UiTextKey.ShareFailed to "Ошибка отправки",
    UiTextKey.ShareWordWith to "Поделиться словом с {username}",
    UiTextKey.ShareMaterialWith to "Поделиться материалом с {username}",
    UiTextKey.ShareAcceptButton to "Принять",
    UiTextKey.ShareDismissButton to "Отклонить",
    UiTextKey.ShareAccepted to "Добавлено в коллекцию",
    UiTextKey.ShareDismissed to "Элемент отклонён",
    UiTextKey.ShareActionFailed to "Ошибка действия",
    UiTextKey.ShareTypeWord to "Слово",
    UiTextKey.ShareTypeLearningSheet to "Учебный материал",
    UiTextKey.ShareReceivedFrom to "От: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} новых элементов!",
    UiTextKey.ShareViewFullMaterial to "Нажмите \"Смотреть\" для просмотра полного материала",
    UiTextKey.ShareDeleteItemTitle to "Удалить элемент",
    UiTextKey.ShareDeleteItemMessage to "Удалить этот элемент? Это необратимо",
    UiTextKey.ShareDeleteButton to "Удалить",
    UiTextKey.ShareViewButton to "Смотреть",
    UiTextKey.ShareItemNotFound to "Элемент не найден",
    UiTextKey.ShareNoContent to "Нет содержимого в материале",
    UiTextKey.ShareSaveToSelf to "Сохранить во входящие",
    UiTextKey.ShareSavedToSelf to "Сохранено во входящие!",

    // My profile
    UiTextKey.MyProfileTitle to "Мой профиль",
    UiTextKey.MyProfileUserId to "User ID",
    UiTextKey.MyProfileUsername to "Имя пользователя",
    UiTextKey.MyProfileDisplayName to "Отображаемое имя",
    UiTextKey.MyProfileCopyUserId to "Копировать ID",
    UiTextKey.MyProfileCopyUsername to "Копировать имя",
    UiTextKey.MyProfileShare to "Поделиться профилем",
    UiTextKey.MyProfileCopied to "Скопировано!",
    UiTextKey.MyProfileLanguages to "Языки",
    UiTextKey.MyProfilePrimaryLanguage to "Основной язык",
    UiTextKey.MyProfileLearningLanguages to "Изучаемые языки",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Страница друзей",
    UiTextKey.FriendsInfoMessage to "• Потяните вниз для обновления списка, запросов и статуса\n" +
            "• Нажмите на карточку для открытия чата\n" +
            "• Красная точка (●) — непрочитанные; ✓✓ — все прочитаны\n" +
            "• 📥 для входящих; ✓✓ для сброса значков\n" +
            "• 🚫 для блокировки — удаляет друга и блокирует взаимодействие\n" +
            "• Блокировка также удаляет историю чата\n" +
            "• Значок корзины для режима удаления\n" +
            "• Удаление друга удаляет все сообщения\n" +
            "• Значок поиска для поиска по имени или ID\n" +
            "• Уведомления выключены по умолчанию — включите в Настройках\n",
    UiTextKey.FriendsEmptyTitle to "Друзей пока нет",
    UiTextKey.FriendsEmptyMessage to "Нажмите \"Добавить друга\" для поиска по имени или ID\n",
    UiTextKey.FriendsInfoGotItButton to "Понятно",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Страница обучения",
    UiTextKey.LearningInfoMessage to "• Потяните вниз для обновления списка\n" +
            "• Каждая карточка показывает язык и счётчик\n" +
            "• \"Создать\" для материалов (первый раз бесплатно)\n" +
            "• Пересоздание требует 5 дополнительных записей\n" +
            "• Кнопка материала для открытия созданного\n" +
            "• После создания материала можно пройти тест",
    UiTextKey.LearningEmptyTitle to "Нет истории переводов",
    UiTextKey.LearningEmptyMessage to "Начните переводить для создания записей\n" +
            "Материалы создаются из истории\n" +
            "После перевода потяните вниз для обновления",
    UiTextKey.LearningInfoGotItButton to "Понятно",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Страница словаря",
    UiTextKey.WordBankInfoMessage to "• Потяните вниз для обновления списка языков\n" +
            "• Выберите язык для просмотра или создания\n" +
            "• Словарь создаётся из истории\n" +
            "• Обновление требует 20 дополнительных записей\n" +
            "• Добавляйте свои слова вручную\n" +
            "• Делитесь словами с друзьями",
    UiTextKey.WordBankInfoGotItButton to "Понятно",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Входящие",
    UiTextKey.ShareInboxInfoMessage to "• Потяните вниз для обновления\n" +
            "• Материалы от друзей появляются здесь\n" +
            "• Принимайте или отклоняйте слова\n" +
            "• \"Смотреть\" для материалов и тестов\n" +
            "• Красная точка (●) для новых/непрочитанных\n" +
            "• Подтверждение перед отклонением слов",
    UiTextKey.ShareInboxInfoGotItButton to "Понятно",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Видимость профиля",
    UiTextKey.MyProfileVisibilityPublic to "Публичный",
    UiTextKey.MyProfileVisibilityPrivate to "Приватный",
    UiTextKey.MyProfileVisibilityDescription to "Публичный: Любой может найти и добавить вас\nПриватный: Вы не отображаетесь в поиске",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Отклонить слово",
    UiTextKey.ShareDismissWordMessage to "Отклонить общее слово? Это необратимо",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Язык: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Закрыть",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Подключено или ожидание",
    UiTextKey.AccessibilityNewMessages to "Новые сообщения",
    UiTextKey.AccessibilityNewReleasesIcon to "Индикатор новых элементов",
    UiTextKey.AccessibilitySuccessIcon to "Успех",
    UiTextKey.AccessibilityErrorIcon to "Ошибка",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Тип общего элемента",
    UiTextKey.AccessibilityAddCustomWords to "Добавить свои слова",
    UiTextKey.AccessibilityWordBankExists to "Словарь существует",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "RU.Отзыв",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Настройки уведомлений",
    UiTextKey.FriendsNotifSettingsTitle to "Настройки уведомлений",
    UiTextKey.FriendsNotifNewMessages to "Новые сообщения в чате",
    UiTextKey.FriendsNotifFriendRequests to "Принятые запросы",
    UiTextKey.FriendsNotifRequestAccepted to "Одобренные запросы",
    UiTextKey.FriendsNotifSharedInbox to "Новые общие элементы",
    UiTextKey.FriendsNotifCloseButton to "Готово",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Значки в приложении (красные точки)",
    UiTextKey.InAppBadgeMessages to "Значок непрочитанных сообщений",
    UiTextKey.InAppBadgeFriendRequests to "Значок запросов дружбы",
    UiTextKey.InAppBadgeSharedInbox to "Значок входящих",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Войдите для продолжения",
    UiTextKey.ErrorSaveFailedRetry to "Ошибка сохранения. Повторите",
    UiTextKey.ErrorLoadFailedRetry to "Ошибка загрузки. Повторите",
    UiTextKey.ErrorNetworkRetry to "Ошибка сети. Проверьте соединение",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Нужно ещё {needed} переводов для создания материала",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Перейти к непрерывному разговору →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Очистить разговор",
    UiTextKey.ChatClearConversationTitle to "Очистить разговор",
    UiTextKey.ChatClearConversationMessage to "Скрыть все сообщения? Они останутся скрытыми при повторном открытии. Не влияет на другого собеседника",
    UiTextKey.ChatClearConversationConfirm to "Очистить всё",
    UiTextKey.ChatClearConversationSuccess to "Разговор очищен",

    // Block user
    UiTextKey.BlockUserButton to "Заблокировать",
    UiTextKey.BlockUserTitle to "Заблокировать пользователя?",
    UiTextKey.BlockUserMessage to "Заблокировать {username}? Будет удалён из списка и заблокирован",
    UiTextKey.BlockUserConfirm to "Заблокировать",
    UiTextKey.BlockUserSuccess to "Заблокирован и удалён из списка",
    UiTextKey.BlockedUsersTitle to "Заблокированные",
    UiTextKey.BlockedUsersEmpty to "Нет заблокированных",
    UiTextKey.UnblockUserButton to "Разблокировать",
    UiTextKey.UnblockUserTitle to "Разблокировать?",
    UiTextKey.UnblockUserMessage to "Разблокировать {username}? Сможет снова отправлять запросы",
    UiTextKey.UnblockUserSuccess to "Разблокирован",
    UiTextKey.BlockedUsersManageButton to "Управление блокировками",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Заметка к запросу (необязательно)",
    UiTextKey.FriendsRequestNotePlaceholder to "Добавить короткую заметку...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Материал готов! Нажмите для открытия",
    UiTextKey.GenerationBannerWordBank to "Словарь готов! Нажмите для просмотра",
    UiTextKey.GenerationBannerQuiz to "Тест готов! Нажмите для начала",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Уведомления",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Китайский (Традиционный)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Система друзей",
    UiTextKey.HelpFriendSystemBody to "• Поиск друзей по имени или ID\n" +
            "• Отправка, принятие или отклонение запросов\n" +
            "• Чат с переводом\n" +
            "• Обмен словами и учебными материалами\n" +
            "• Управление общими материалами во входящих\n" +
            "• Красная точка (●) для нового/непрочитанного\n" +
            "• Потяните вниз для обновления",
    UiTextKey.HelpProfileVisibilityTitle to "Видимость профиля",
    UiTextKey.HelpProfileVisibilityBody to "• Установите профиль как публичный или приватный в Настройках\n" +
            "• Публичный: Любой может найти вас\n" +
            "• Приватный: Вы не отображаетесь в поиске\n" +
            "• Приватный: поделитесь ID для добавления",
    UiTextKey.HelpColorPalettesTitle to "Темы и монеты",
    UiTextKey.HelpColorPalettesBody to "• 1 бесплатная тема: Sky Blue (по умолчанию)\n" +
            "• 10 тем за 10 монет каждая\n" +
            "• Зарабатывайте монеты в тестах\n" +
            "• Тратьте на темы и расширение истории\n" +
            "• Автоматическая тема: светлая 6-18, тёмная 18-6",
    UiTextKey.HelpPrivacyTitle to "Конфиденциальность и данные",
    UiTextKey.HelpPrivacyBody to "• Голос используется только для распознавания, не хранится постоянно\n" +
            "• OCR обрабатывается на устройстве (безопасно)\n" +
            "• Можно удалить аккаунт и данные в любой момент\n" +
            "• Приватный режим: не отображаетесь в поиске\n" +
            "• Все данные безопасно синхронизируются через Firebase",
    UiTextKey.HelpAppVersionTitle to "Версия приложения",
    UiTextKey.HelpAppVersionNotes to "• Лимит истории: от 30 до 60 записей (расширьте монетами)\n" +
            "• Имена пользователей уникальны — смените для освобождения старого\n" +
            "• Автоматический выход при обновлениях безопасности\n" +
            "• Все переводы через Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Мгновенный перевод",
    UiTextKey.OnboardingPage1Desc to "Быстрый перевод для коротких фраз. Непрерывный разговор для двусторонних бесед",
    UiTextKey.OnboardingPage2Title to "Изучайте словарь",
    UiTextKey.OnboardingPage2Desc to "Создавайте списки слов и тесты из истории",
    UiTextKey.OnboardingPage3Title to "Общайтесь с друзьями",
    UiTextKey.OnboardingPage3Desc to "Чат, обмен словами и совместное обучение",
    UiTextKey.OnboardingSkipButton to "Пропустить",
    UiTextKey.OnboardingNextButton to "Далее",
    UiTextKey.OnboardingGetStartedButton to "Начать",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 С возвращением, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Пользователь:",
    UiTextKey.ChatUserIdLabel to "User ID:",
    UiTextKey.ChatLearningLabel to "Изучает:",
    UiTextKey.ChatBlockedMessage to "Невозможно отправить сообщение этому пользователю",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Поиск",
    UiTextKey.CustomWordsEmptyState to "Своих слов пока нет",
    UiTextKey.CustomWordsEmptyHint to "Нажмите + для добавления слова",
    UiTextKey.CustomWordsNoSearchResults to "Слова не найдены",
    UiTextKey.AddCustomWordHintTemplate to "Введите слово на {from} и перевод на {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} записей",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "Email: {email}",
    UiTextKey.ProfileUsernameHintFull to "Имя пользователя для друзей (3–20 символов, буквы/цифры/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Нет вариантов голоса для этого языка",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "Приложение обновлено. Войдите снова",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Лимит избранного",
    UiTextKey.FavoritesLimitMessage to "Максимум 20 избранных. Удалите другие для добавления",
    UiTextKey.FavoritesLimitGotIt to "Понятно",
    UiTextKey.FavoritesInfoTitle to "Информация об избранном",
    UiTextKey.FavoritesInfoMessage to "Максимум 20 избранных (записи и сеансы). Ограничение для нагрузки базы. Удалите для добавления новых",
    UiTextKey.FavoritesInfoGotIt to "Понятно",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Невозможно изменить",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "Основной язык можно менять каждые 30 дней. Осталось {days} дней",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "Основной язык можно менять каждые 30 дней. Осталось {hours} часов",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Подтвердите смену языка",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Смена основного языка необратима в течение 30 дней. Продолжить?",

    // Bottom navigation
    UiTextKey.NavHome to "Главная",
    UiTextKey.NavTranslate to "Перевод",
    UiTextKey.NavLearn to "Учёба",
    UiTextKey.NavFriends to "Друзья",
    UiTextKey.NavSettings to "Настройки",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Требуется разрешение камеры",
    UiTextKey.CameraPermissionMessage to "Разрешите доступ к камере для распознавания текста",
    UiTextKey.CameraPermissionGrant to "Разрешить",
    UiTextKey.MicPermissionMessage to "Требуется разрешение микрофона для распознавания речи",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Удалить {count} выбранных элементов? Это необратимо",
    UiTextKey.WordBankDeleteConfirm to "Удалить \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Принять все",
    UiTextKey.FriendsRejectAllButton to "Отклонить все",
    UiTextKey.ChatBlockedCannotSend to "Невозможно отправить сообщение",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Разблокировать {name}?",
    UiTextKey.ShopUnlockCost to "Стоимость: {cost} монет",
    UiTextKey.ShopYourCoins to "Мои монеты: {coins}",
    UiTextKey.ShopUnlockButton to "Разблокировать",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Основной язык",
    UiTextKey.HelpPrimaryLanguageBody to "• Основной язык используется для описаний в обучении\n" +
            "• Можно менять каждые 30 дней для единообразия\n" +
            "• Измените в Настройках\n" +
            "• Настройка применяется на всех страницах",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Совет: Для лучшего распознавания установите \"Язык источника\" на язык сканируемого текста",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Невозможно изменить",
    UiTextKey.SettingsUsernameCooldownMessage to "Имя пользователя можно менять каждые 30 дней. Осталось {days} дней",
    UiTextKey.SettingsUsernameCooldownMessageHours to "Имя пользователя можно менять каждые 30 дней. Осталось {hours} часов",
    UiTextKey.SettingsUsernameConfirmTitle to "Подтвердите смену имени",
    UiTextKey.SettingsUsernameConfirmMessage to "Смена имени пользователя необратима в течение 30 дней. Продолжить?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Нет подключения к интернету. Проверьте соединение",
    UiTextKey.ErrorPermissionDenied to "Разрешение отклонено для этого действия",
    UiTextKey.ErrorSessionExpired to "Сеанс истёк. Войдите снова",
    UiTextKey.ErrorItemNotFound to "Элемент не найден. Возможно, он был удалён",
    UiTextKey.ErrorAccessDenied to "Доступ запрещён",
    UiTextKey.ErrorAlreadyFriends to "Уже друзья",
    UiTextKey.ErrorUserBlocked to "Не разрешено. Пользователь может быть заблокирован",
    UiTextKey.ErrorRequestNotFound to "Запрос больше не существует",
    UiTextKey.ErrorRequestAlreadyHandled to "Запрос уже обработан",
    UiTextKey.ErrorNotAuthorized to "У вас нет прав для этого действия",
    UiTextKey.ErrorRateLimited to "Слишком много запросов. Повторите позже",
    UiTextKey.ErrorInvalidInput to "Неверный ввод. Проверьте и повторите",
    UiTextKey.ErrorOperationNotAllowed to "Эта операция сейчас не разрешена",
    UiTextKey.ErrorTimeout to "Время истекло. Повторите попытку",
    UiTextKey.ErrorSendMessageFailed to "Ошибка отправки сообщения. Повторите",
    UiTextKey.ErrorFriendRequestSent to "Запрос уже отправлен!",
    UiTextKey.ErrorFriendRequestFailed to "Ошибка отправки запроса",
    UiTextKey.ErrorFriendRemoved to "Друг удалён",
    UiTextKey.ErrorFriendRemoveFailed to "Ошибка удаления. Проверьте соединение",
    UiTextKey.ErrorBlockSuccess to "Пользователь заблокирован",
    UiTextKey.ErrorBlockFailed to "Ошибка блокировки. Повторите",
    UiTextKey.ErrorUnblockSuccess to "Разблокирован",
    UiTextKey.ErrorUnblockFailed to "Ошибка разблокировки. Повторите",
    UiTextKey.ErrorAcceptRequestSuccess to "Запрос принят!",
    UiTextKey.ErrorAcceptRequestFailed to "Ошибка принятия. Повторите",
    UiTextKey.ErrorRejectRequestSuccess to "Запрос отклонён",
    UiTextKey.ErrorRejectRequestFailed to "Ошибка отклонения. Повторите",
    UiTextKey.ErrorOfflineMessage to "Вы офлайн. Некоторые функции могут быть недоступны",
    UiTextKey.ErrorChatDeletionFailed to "Ошибка очистки. Повторите",
    UiTextKey.ErrorGenericRetry to "Произошла ошибка. Повторите попытку",
)
