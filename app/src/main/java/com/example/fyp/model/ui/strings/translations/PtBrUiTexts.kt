package com.example.fyp.model.ui

/**
 * Brazilian Portuguese (pt-BR) UI text map — Textos de interface em Português do Brasil.
 * All strings are hardcoded — no API translation required.
 * Order matches UiTextKey enum ordinal.
 */
val PtBrUiTexts: Map<UiTextKey, String> = mapOf(
    // Core UI
    UiTextKey.AzureRecognizeButton to "Usar microfone",
    UiTextKey.CopyButton to "Copiar",
    UiTextKey.SpeakScriptButton to "Ler original",
    UiTextKey.TranslateButton to "Traduzir",
    UiTextKey.CopyTranslationButton to "Copiar tradução",
    UiTextKey.SpeakTranslationButton to "Ler tradução",
    UiTextKey.RecognizingStatus to "Gravando...por favor fale, irá parar automaticamente",
    UiTextKey.TranslatingStatus to "Traduzindo...",
    UiTextKey.SpeakingOriginalStatus to "Lendo texto original...",
    UiTextKey.SpeakingTranslationStatus to "Lendo tradução...",
    UiTextKey.SpeakingLabel to "Falando",
    UiTextKey.FinishedSpeakingOriginal to "Leitura do texto original concluída",
    UiTextKey.FinishedSpeakingTranslation to "Leitura da tradução concluída",
    UiTextKey.TtsErrorTemplate to "Erro de voz: %s",

    // Language labels
    UiTextKey.AppUiLanguageLabel to "Idioma do app",
    UiTextKey.DetectLanguageLabel to "Detectar idioma",
    UiTextKey.TranslateToLabel to "Traduzir para",

    // Language names
    UiTextKey.LangEnUs to "Inglês",
    UiTextKey.LangZhHk to "Cantonês",
    UiTextKey.LangJaJp to "Japonês",
    UiTextKey.LangZhCn to "Chinês (Simplificado)",
    UiTextKey.LangFrFr to "Francês",
    UiTextKey.LangDeDe to "Alemão",
    UiTextKey.LangKoKr to "Coreano",
    UiTextKey.LangEsEs to "Espanhol",
    UiTextKey.LangIdId to "Indonésio",
    UiTextKey.LangViVn to "Vietnamita",
    UiTextKey.LangThTh to "Tailandês",
    UiTextKey.LangFilPh to "Filipino",
    UiTextKey.LangMsMy to "Malaio",
    UiTextKey.LangPtBr to "Português",
    UiTextKey.LangItIt to "Italiano",
    UiTextKey.LangRuRu to "Russo",

    // Navigation
    UiTextKey.NavHistory to "Histórico",
    UiTextKey.NavLogin to "Entrar",
    UiTextKey.NavLogout to "Sair",
    UiTextKey.NavBack to "Voltar",
    UiTextKey.ActionCancel to "Cancelar",
    UiTextKey.ActionDelete to "Excluir",
    UiTextKey.ActionOpen to "Abrir",
    UiTextKey.ActionName to "Nomear",
    UiTextKey.ActionSave to "Salvar",
    UiTextKey.ActionConfirm to "Confirmar",


    // Speech
    UiTextKey.SpeechInputPlaceholder to "Digite aqui ou use o microfone...",
    UiTextKey.SpeechTranslatedPlaceholder to "A tradução aparecerá aqui...",
    UiTextKey.StatusAzureErrorTemplate to "Erro Azure: %s",
    UiTextKey.StatusTranslationErrorTemplate to "Erro de tradução: %s",
    UiTextKey.StatusLoginRequiredTranslation to "Entre para traduzir",
    UiTextKey.StatusRecognizePreparing to "Preparando microfone...(não fale ainda)",
    UiTextKey.StatusRecognizeListening to "Ouvindo...por favor fale",

    // Pagination
    UiTextKey.PaginationPrevLabel to "Anterior",
    UiTextKey.PaginationNextLabel to "Próximo",
    UiTextKey.PaginationPageLabelTemplate to "Página {page} / {total}",

    // Toast
    UiTextKey.ToastCopied to "Copiado",
    UiTextKey.DisableText to "Entre para traduzir e salvar o histórico",

    // Error
    UiTextKey.ErrorRetryButton to "Tentar novamente",
    UiTextKey.ErrorGenericMessage to "Ocorreu um erro. Tente novamente",

    // Shop
    UiTextKey.ShopTitle to "Loja",
    UiTextKey.ShopCoinBalance to "Minhas moedas",
    UiTextKey.ShopHistoryExpansionTitle to "Expandir histórico",
    UiTextKey.ShopHistoryExpansionDesc to "Aumente o limite de histórico para ver mais traduções",
    UiTextKey.ShopCurrentLimit to "Limite atual: {limit} registros",
    UiTextKey.ShopMaxLimit to "Limite máximo:",
    UiTextKey.ShopBuyHistoryExpansion to "Comprar (+{increment} registros, {cost} moedas)",
    UiTextKey.ShopInsufficientCoins to "Moedas insuficientes",
    UiTextKey.ShopMaxLimitReached to "Limite máximo atingido",
    UiTextKey.ShopHistoryExpandedTitle to "Expandido com sucesso!",
    UiTextKey.ShopHistoryExpandedMessage to "Seu histórico agora é de {limit} registros! Você pode ver mais traduções!",
    UiTextKey.ShopColorPaletteTitle to "Temas de cores",
    UiTextKey.ShopColorPaletteDesc to "Escolha um tema de cores. 10 moedas cada",
    UiTextKey.ShopEntry to "Loja",

    // Voice settings
    UiTextKey.VoiceSettingsTitle to "Configurações de voz",
    UiTextKey.VoiceSettingsDesc to "Escolha a voz para cada idioma",

    // Instructions
    UiTextKey.SpeechInstructions to "Toque no microfone para reconhecimento de voz. Se a detecção automática não atualizar após trocar, toque em atualizar no canto superior direito",
    UiTextKey.HomeInstructions to "Selecione um recurso para começar",
    UiTextKey.ContinuousInstructions to "Escolha dois idiomas e inicie a conversa",

    // Home
    UiTextKey.HomeTitle to "Tradução Instantânea",
    UiTextKey.HelpTitle to "Ajuda",
    UiTextKey.SpeechTitle to "Tradução Rápida",
    UiTextKey.HomeStartButton to "Começar a traduzir",
    UiTextKey.HomeFeaturesTitle to "Recursos",
    UiTextKey.HomeDiscreteDescription to "Traduza textos e áudios curtos",
    UiTextKey.HomeContinuousDescription to "Conversas bilaterais em tempo real",
    UiTextKey.HomeLearningDescription to "Crie materiais e quizzes do histórico",

    // Help
    UiTextKey.HelpCurrentTitle to "Recursos atuais",
    UiTextKey.HelpCautionTitle to "Atenção",
    UiTextKey.HelpCurrentFeatures to "Recursos atuais:\n" +
            "  • Tradução Rápida: reconhecimento de voz e tradução\n" +
            "  • Conversa Contínua: tradução de voz bilateral\n" +
            "  • Histórico: veja traduções anteriores\n" +
            "  • Materiais de Aprendizagem: crie vocabulário e quizzes\n\n" +
            "Tradução:\n" +
            "  • Reconhecimento de voz Azure AI\n" +
            "  • Serviço de Tradução Azure\n",
    UiTextKey.HelpCaution to "Atenção:\n" +
            "  • Reconhecimento de voz requer internet\n" +
            "  • Traduções em cache funcionam offline\n" +
            "  • Verifique traduções importantes com especialistas\n\n" +
            "Conta e dados:\n" +
            "  • Login necessário para histórico, aprendizagem e moedas\n" +
            "  • Armazenado com segurança no Firebase Firestore\n\n" +
            "Solução de problemas:\n" +
            "  • Se não funcionar, reinicie o app\n",
    UiTextKey.HelpNotesTitle to "Dicas",
    UiTextKey.HelpNotes to "💡 Dicas de uso:\n\n" +
            "Para melhores traduções:\n" +
            "  • Fale claramente em velocidade normal\n" +
            "  • Reduza ruído para melhor reconhecimento\n" +
            "  • Tradução rápida para frases curtas\n\n" +
            "Idioma do app:\n" +
            "  • Padrão: Inglês, outros via IA\n" +
            "  • Tradução manual para Cantonês\n" +
            "Atualizações e feedback:\n" +
            "  • Versão do app em Configurações → Sobre\n" +
            "  • Envie feedback em Configurações → Feedback\n",

    // Feedback
    UiTextKey.FeedbackTitle to "Feedback",
    UiTextKey.FeedbackDesc to "Obrigado! Compartilhe suas sugestões, bugs ou avaliações",
    UiTextKey.FeedbackMessagePlaceholder to "Digite seu feedback...",
    UiTextKey.FeedbackSubmitButton to "Enviar",
    UiTextKey.FeedbackSubmitting to "Enviando...",
    UiTextKey.FeedbackSuccessTitle to "Obrigado!",
    UiTextKey.FeedbackSuccessMessage to "Feedback enviado com sucesso. Obrigado!",
    UiTextKey.FeedbackErrorTitle to "Falha ao enviar",
    UiTextKey.FeedbackErrorMessage to "Falha ao enviar. Verifique a conexão e tente novamente",
    UiTextKey.FeedbackMessageRequired to "Mensagem de feedback obrigatória",

    // Continuous mode
    UiTextKey.ContinuousTitle to "Conversa Contínua",
    UiTextKey.ContinuousStartButton to "Iniciar conversa",
    UiTextKey.ContinuousStopButton to "Parar gravação",
    UiTextKey.ContinuousStartScreenButton to "Conversa Contínua",
    UiTextKey.ContinuousPersonALabel to "Falante A",
    UiTextKey.ContinuousPersonBLabel to "Falante B",
    UiTextKey.ContinuousCurrentStringLabel to "Texto atual:",
    UiTextKey.ContinuousSpeakerAName to "Pessoa A",
    UiTextKey.ContinuousSpeakerBName to "Pessoa B",
    UiTextKey.ContinuousTranslationSuffix to " · Tradução",
    UiTextKey.ContinuousPreparingMicText to "Preparando microfone...(não fale ainda)",
    UiTextKey.ContinuousTranslatingText to "Traduzindo...",

    // History
    UiTextKey.HistoryTitle to "Histórico",
    UiTextKey.HistoryTabDiscrete to "Tradução Rápida",
    UiTextKey.HistoryTabContinuous to "Conversa Contínua",
    UiTextKey.HistoryNoContinuousSessions to "Nenhuma sessão de conversa ainda",
    UiTextKey.HistoryNoDiscreteRecords to "Nenhuma tradução ainda",
    UiTextKey.DialogDeleteRecordTitle to "Excluir registro?",
    UiTextKey.DialogDeleteRecordMessage to "Esta ação não pode ser desfeita",
    UiTextKey.DialogDeleteSessionTitle to "Excluir sessão?",
    UiTextKey.DialogDeleteSessionMessage to "Todos os registros nesta sessão serão excluídos. Não pode ser desfeito",
    UiTextKey.HistoryDeleteSessionButton to "Excluir",
    UiTextKey.HistoryNameSessionTitle to "Nomear",
    UiTextKey.HistorySessionNameLabel to "Nome da sessão",
    UiTextKey.HistorySessionTitleTemplate to "Sessão {id}",
    UiTextKey.HistoryItemsCountTemplate to "{count} registros",

    // Filter
    UiTextKey.FilterDropdownDefault to "Todos os idiomas",
    UiTextKey.FilterTitle to "Filtrar histórico",
    UiTextKey.FilterLangDrop to "Idioma",
    UiTextKey.FilterKeyword to "Palavra-chave",
    UiTextKey.FilterApply to "Aplicar",
    UiTextKey.FilterCancel to "Cancelar",
    UiTextKey.FilterClear to "Limpar filtro",
    UiTextKey.FilterHistoryScreenTitle to "Filtro",

    // Auth
    UiTextKey.AuthLoginTitle to "Entrar",
    UiTextKey.AuthRegisterTitle to "Registrar (Suspenso)",
    UiTextKey.AuthLoginHint to "Use seu e-mail e senha registrados",
    UiTextKey.AuthRegisterRules to "Registro suspenso durante o desenvolvimento\nAviso: A senha não pode ser recuperada se o e-mail estiver errado\n" +
            "Regras de registro:\n" +
            "• Formato de e-mail válido (ex: nome@exemplo.com)\n" +
            "• Senha com pelo menos 6 caracteres\n" +
            "• Confirmação de senha deve coincidir",
    UiTextKey.AuthEmailLabel to "E-mail",
    UiTextKey.AuthPasswordLabel to "Senha",
    UiTextKey.AuthConfirmPasswordLabel to "Confirmar senha",
    UiTextKey.AuthLoginButton to "Entrar",
    UiTextKey.AuthRegisterButton to "Registrar",
    UiTextKey.AuthToggleToRegister to "Não tem conta? Registre-se (Suspenso)",
    UiTextKey.AuthToggleToLogin to "Já tem conta? Entre",
    UiTextKey.AuthErrorPasswordsMismatch to "As senhas não coincidem",
    UiTextKey.AuthErrorPasswordTooShort to "Senha precisa de pelo menos 6 caracteres",
    UiTextKey.AuthRegistrationDisabled to "Registro suspenso durante o desenvolvimento",
    UiTextKey.AuthResetEmailSent to "E-mail de redefinição enviado (se a conta existir). Verifique sua caixa de entrada",

    // Password reset
    UiTextKey.ForgotPwText to "Esqueceu a senha?",
    UiTextKey.ResetPwTitle to "Redefinir senha",
    UiTextKey.ResetPwText to "Digite o e-mail da conta. Enviaremos um link de redefinição\nCertifique-se de que o e-mail foi registrado\n",
    UiTextKey.ResetSendingText to "Enviando...",
    UiTextKey.ResetSendText to "Enviar e-mail de redefinição",

    // Settings
    UiTextKey.SettingsTitle to "Configurações",
    UiTextKey.SettingsPrimaryLanguageTitle to "Idioma principal",
    UiTextKey.SettingsPrimaryLanguageDesc to "Usado para descrições e guias de aprendizagem",
    UiTextKey.SettingsPrimaryLanguageLabel to "Idioma principal",
    UiTextKey.SettingsFontSizeTitle to "Tamanho da fonte",
    UiTextKey.SettingsFontSizeDesc to "Ajuste o tamanho da fonte (sincronizado entre dispositivos)",
    UiTextKey.SettingsScaleTemplate to "Escala: {pct}%",
    UiTextKey.SettingsColorPaletteTitle to "Temas de cores",
    UiTextKey.SettingsColorPaletteDesc to "Escolha um tema de cores. 10 moedas cada",
    UiTextKey.SettingsColorCostTemplate to "{cost} moedas",
    UiTextKey.SettingsColorUnlockButton to "Desbloquear",
    UiTextKey.SettingsColorSelectButton to "Selecionar",
    UiTextKey.SettingsColorAlreadyUnlocked to "Já desbloqueado",
    UiTextKey.SettingsPreviewHeadline to "Título: Texto grande",
    UiTextKey.SettingsPreviewBody to "Corpo: Texto normal",
    UiTextKey.SettingsPreviewLabel to "Rótulo: Texto pequeno",
    UiTextKey.SettingsAboutTitle to "Sobre",
    UiTextKey.SettingsAppVersion to "Talk & Learn Translator v",
    UiTextKey.SettingsSyncInfo to "Conectado. Configurações salvas e sincronizadas automaticamente",
    UiTextKey.SettingsThemeTitle to "Tema",
    UiTextKey.SettingsThemeDesc to "Escolha o tema: Sistema, Claro, Escuro ou Agendado",
    UiTextKey.SettingsThemeSystem to "Sistema",
    UiTextKey.SettingsThemeLight to "Claro",
    UiTextKey.SettingsThemeDark to "Escuro",
    UiTextKey.SettingsThemeScheduled to "Agendado",
    UiTextKey.SettingsResetPW to "Redefinir senha",
    UiTextKey.SettingsQuickLinks to "Outras configurações",
    UiTextKey.SettingsNotLoggedInWarning to "Entre para ver as configurações da conta. O idioma do app pode ser alterado sem login",
    UiTextKey.SettingsVoiceTitle to "Configurações de voz",
    UiTextKey.SettingsVoiceDesc to "Escolha a voz para cada idioma",
    UiTextKey.SettingsVoiceLanguageLabel to "Idioma",
    UiTextKey.SettingsVoiceSelectLabel to "Voz",
    UiTextKey.SettingsVoiceDefault to "Padrão",

    // Learning
    UiTextKey.LearningTitle to "Aprendizagem",
    UiTextKey.LearningHintCount to "(*) Contagem = traduções envolvendo este idioma",
    UiTextKey.LearningErrorTemplate to "Erro: %s",
    UiTextKey.LearningGenerate to "Gerar",
    UiTextKey.LearningRegenerate to "Regenerar",
    UiTextKey.LearningGenerating to "Gerando...",
    UiTextKey.LearningOpenSheetTemplate to "Material {speclanguage}",
    UiTextKey.LearningSheetTitleTemplate to "Material de aprendizagem {speclanguage}",
    UiTextKey.LearningSheetPrimaryTemplate to "Idioma principal: {speclanguage}",
    UiTextKey.LearningSheetHistoryCountTemplate to "Contagem atual: {nowCount} (na geração: {savedCount})",
    UiTextKey.LearningSheetNoContent to "Sem conteúdo",
    UiTextKey.LearningSheetRegenerate to "Regenerar",
    UiTextKey.LearningSheetGenerating to "Gerando...",
    UiTextKey.LearningSheetWhatIsThisTitle to "📚 O que é isto?",
    UiTextKey.LearningSheetWhatIsThisDesc to "Materiais de aprendizagem são gerados do seu histórico de traduções. Incluem vocabulário, definições, exemplos e notas gramaticais. Teste seu conhecimento com quizzes!",
    UiTextKey.LearningRegenBlockedTitle to "Não é possível regenerar",
    UiTextKey.LearningRegenBlockedMessage to "Necessário pelo menos 5 registros adicionais para regenerar. Ainda precisa de {needed}",
    UiTextKey.LearningRegenNeedMoreRecords to "⚠️ Precisa de mais {needed} registros para regenerar (necessário mais 5)",
    UiTextKey.LearningRegenCountNotHigher to "⚠️ A contagem deve ser maior que na última geração",
    UiTextKey.LearningRegenInfoTitle to "Regras de regeneração",
    UiTextKey.LearningRegenInfoMessage to "Regenerar materiais:\n\n• Primeira vez: a qualquer momento\n• Regenerar: precisa de mais 5 registros\n\nO botão ficará verde quando pronto. Se estiver cinza, faça mais traduções!\n\n💡 Dica: Se a contagem não atualizar, reinicie o app",
    UiTextKey.QuizRegenBlockedSameMaterial to "❌ Quiz já gerado para esta versão. Regenere os materiais para um novo quiz",

    // Quiz
    UiTextKey.QuizTitleTemplate to "Quiz: {language}",
    UiTextKey.QuizOpenButton to "📝 Quiz",
    UiTextKey.QuizGenerateButton to "🔄 Gerar quiz",
    UiTextKey.QuizGenerating to "⏳ Gerando...",
    UiTextKey.QuizUpToDate to "✓ Atualizado",
    UiTextKey.QuizBlocked to "🚫 Bloqueado",
    UiTextKey.QuizWait to "⏳ Aguarde...",
    UiTextKey.QuizMaterialsQuizTemplate to "Material: {materials} | Quiz: {quiz}",
    UiTextKey.QuizCanEarnCoins to "🪙 Pode ganhar moedas!",
    UiTextKey.QuizNeedMoreRecordsTemplate to "🪙 Precisa de mais {count} para moedas",
    UiTextKey.QuizCancelButton to "Cancelar",
    UiTextKey.QuizPreviousButton to "Anterior",
    UiTextKey.QuizNextButton to "Próximo",
    UiTextKey.QuizSubmitButton to "Enviar",
    UiTextKey.QuizRetakeButton to "Refazer",
    UiTextKey.QuizBackButton to "Voltar",
    UiTextKey.QuizLoadingText to "Carregando quiz...",
    UiTextKey.QuizGeneratingText to "Gerando quiz...",
    UiTextKey.QuizNoMaterialsTitle to "Nenhum material encontrado",
    UiTextKey.QuizNoMaterialsMessage to "Gere materiais de aprendizagem primeiro antes de tentar o quiz",
    UiTextKey.QuizErrorTitle to "⚠️ Erro no quiz",
    UiTextKey.QuizErrorSuggestion to "Sugestão: Use o botão acima para gerar o quiz",
    UiTextKey.QuizCompletedTitle to "Quiz concluído!",
    UiTextKey.QuizAnswerReviewTitle to "Revisão de respostas",
    UiTextKey.QuizYourAnswerTemplate to "Sua resposta: {Answer}",
    UiTextKey.QuizCorrectAnswerTemplate to "Resposta correta: {Answer}",
    UiTextKey.QuizQuestionTemplate to "Pergunta {current} / {total}",
    UiTextKey.QuizCannotRegenTemplate to "⚠️ Não é possível regenerar: material({materials}) < quiz({quiz}), faça mais traduções",
    UiTextKey.QuizAnotherGenInProgress to "⏳ Outra geração em andamento. Aguarde",
    UiTextKey.QuizCoinRulesTitle to "🪙 Regras de moedas",
    UiTextKey.QuizCoinRulesHowToEarn to "✅ Como ganhar:",
    UiTextKey.QuizCoinRulesRequirements to "Requisitos:",
    UiTextKey.QuizCoinRulesCurrentStatus to "Status atual:",
    UiTextKey.QuizCoinRulesCanEarn to "• ✅ Você pode ganhar moedas no próximo quiz!",
    UiTextKey.QuizCoinRulesNeedMoreTemplate to "• Precisa de mais {count} para moedas",
    UiTextKey.QuizCoinRule1Coin to "• 1 moeda por resposta correta",
    UiTextKey.QuizCoinRuleFirstAttempt to "• Apenas a primeira tentativa conta",
    UiTextKey.QuizCoinRuleMatchMaterials to "• Quiz deve corresponder aos materiais",
    UiTextKey.QuizCoinRulePlus10 to "• Pelo menos 10 registros adicionais desde a última recompensa",
    UiTextKey.QuizCoinRuleNoDelete to "• Moedas não são devolvidas se registros forem excluídos",
    UiTextKey.QuizCoinRuleMaterialsTemplate to "• Material: {count} registros",
    UiTextKey.QuizCoinRuleQuizTemplate to "• Quiz: {count} registros",
    UiTextKey.QuizCoinRuleGotIt to "Entendi!",
    UiTextKey.QuizRegenConfirmTitle to "🔄 Regenerar quiz?",
    UiTextKey.QuizRegenCanEarnCoins to "✅ Pode ganhar moedas neste quiz! (apenas primeira tentativa)",
    UiTextKey.QuizRegenCannotEarnCoins to "⚠️ Não pode ganhar moedas neste quiz",
    UiTextKey.QuizRegenNeedMoreTemplate to "Precisa de mais {count} traduções para se qualificar (mais 10 desde a última recompensa)",
    UiTextKey.QuizRegenReminder to "Dica: Pode praticar e refazer, mas moedas apenas na primeira tentativa com registros suficientes",
    UiTextKey.QuizRegenGenerateButton to "Gerar",
    UiTextKey.QuizCoinsEarnedTitle to "✨ Moedas ganhas!",
    UiTextKey.QuizCoinsEarnedMessageTemplate to "Parabéns! Você ganhou {Coins} moedas!",
    UiTextKey.QuizCoinsRule1 to "• 1 moeda por resposta correta na primeira tentativa",
    UiTextKey.QuizCoinsRule2 to "• Sem moedas em tentativas subsequentes",
    UiTextKey.QuizCoinsRule3 to "• Novo quiz requer mais 10 registros",
    UiTextKey.QuizCoinsRule4 to "• Quiz deve corresponder aos materiais",
    UiTextKey.QuizCoinsRule5 to "• Moedas fazem parte do histórico",
    UiTextKey.QuizCoinsGreatButton to "Ótimo!",
    UiTextKey.QuizOutdatedMessage to "Este quiz usa materiais antigos",
    UiTextKey.QuizRecordsLabel to "Registros",

    // History coins
    UiTextKey.HistoryCoinsDialogTitle to "🪙 Minhas moedas",
    UiTextKey.HistoryCoinRulesTitle to "Regras de moedas:",
    UiTextKey.HistoryCoinHowToEarnTitle to "Como ganhar:",
    UiTextKey.HistoryCoinHowToEarnRule1 to "• 1 moeda por resposta correta",
    UiTextKey.HistoryCoinHowToEarnRule2 to "• Apenas primeira tentativa por versão",
    UiTextKey.HistoryCoinHowToEarnRule3 to "• Quiz deve corresponder ao material atual",
    UiTextKey.HistoryCoinAntiCheatTitle to "🔒 Regras anti-fraude:",
    UiTextKey.HistoryCoinAntiCheatRule1 to "• Precisa de 10 novas traduções desde a última recompensa",
    UiTextKey.HistoryCoinAntiCheatRule2 to "• Versão do quiz deve corresponder ao material",
    UiTextKey.HistoryCoinAntiCheatRule3 to "• Excluir registros bloqueia regeneração (exceto se contagem for maior)",
    UiTextKey.HistoryCoinAntiCheatRule4 to "• Sem moedas em tentativas subsequentes",
    UiTextKey.HistoryCoinTipsTitle to "💡 Dicas:",
    UiTextKey.HistoryCoinTipsRule1 to "• Adicione traduções regularmente",
    UiTextKey.HistoryCoinTipsRule2 to "• Estude bem antes da primeira tentativa!",
    UiTextKey.HistoryCoinGotItButton to "Entendi!",

    // History info
    UiTextKey.HistoryInfoTitle to "Informações do histórico",
    UiTextKey.HistoryInfoLimitMessage to "O histórico mostra os {limit} registros mais recentes. Expanda na loja!",
    UiTextKey.HistoryInfoOlderRecordsMessage to "Registros antigos são mantidos mas ocultados para desempenho",
    UiTextKey.HistoryInfoFavoritesMessage to "Se quiser guardar uma tradução, toque em ❤️ para salvar nos favoritos",
    UiTextKey.HistoryInfoViewFavoritesMessage to "Veja os salvos em Configurações → Favoritos",
    UiTextKey.HistoryInfoFilterMessage to "Use o filtro para pesquisar nos {limit} registros exibidos",
    UiTextKey.HistoryInfoGotItButton to "Entendi",

    // Word bank
    UiTextKey.WordBankTitle to "Banco de palavras",
    UiTextKey.WordBankSelectLanguage to "Selecione um idioma para ver ou gerar banco de palavras:",
    UiTextKey.WordBankNoHistory to "Nenhum histórico de tradução ainda",
    UiTextKey.WordBankNoHistoryHint to "Comece a traduzir para criar seu banco de palavras!",
    UiTextKey.WordBankWordsCount to "Palavras",
    UiTextKey.WordBankGenerating to "Gerando...",
    UiTextKey.WordBankGenerate to "Gerar banco de palavras",
    UiTextKey.WordBankRegenerate to "Regenerar banco de palavras",
    UiTextKey.WordBankRefresh to "🔄 Atualizar banco",
    UiTextKey.WordBankEmpty to "Banco de palavras vazio",
    UiTextKey.WordBankEmptyHint to "Toque acima para gerar do histórico",
    UiTextKey.WordBankExample to "Exemplo:",
    UiTextKey.WordBankDifficulty to "Nível:",
    UiTextKey.WordBankFilterCategory to "Categoria",
    UiTextKey.WordBankFilterCategoryAll to "Todas as categorias",
    UiTextKey.WordBankFilterDifficultyLabel to "Nível:",
    UiTextKey.WordBankFilterNoResults to "Nenhuma palavra corresponde ao filtro",
    UiTextKey.WordBankRefreshAvailable to "✅ Atualização disponível!",
    UiTextKey.WordBankRecordsNeeded to "Registros (precisa de 20 para atualizar)",
    UiTextKey.WordBankRegenInfoTitle to "Regras de atualização",
    UiTextKey.WordBankRegenInfoMessage to "Atualizar banco de palavras:\n\n• Primeira vez: a qualquer momento\n• Atualizar: precisa de mais 20 registros\n\nO botão ficará verde quando pronto. Se estiver cinza, faça mais traduções!\n\n💡 Dica: Se a contagem não atualizar, reinicie o app",
    UiTextKey.WordBankHistoryCountTemplate to "Contagem atual: {nowCount} (na geração: {savedCount})",

    // Dialogs
    UiTextKey.DialogLogoutTitle to "Sair?",
    UiTextKey.DialogLogoutMessage to "Será necessário entrar novamente para traduzir e ver o histórico",
    UiTextKey.DialogGenerateOverwriteTitle to "Substituir material?",
    UiTextKey.DialogGenerateOverwriteMessageTemplate to "O material atual será substituído\nGerar material para {speclanguage}?",

    // Profile
    UiTextKey.ProfileTitle to "Perfil",
    UiTextKey.ProfileUsernameLabel to "Nome de usuário",
    UiTextKey.ProfileUsernameHint to "Digite o nome de usuário",
    UiTextKey.ProfileUpdateButton to "Atualizar perfil",
    UiTextKey.ProfileUpdateSuccess to "Perfil atualizado",
    UiTextKey.ProfileUpdateError to "Falha ao atualizar",

    // Account deletion
    UiTextKey.AccountDeleteTitle to "Excluir conta",
    UiTextKey.AccountDeleteWarning to "⚠️ Permanente e irreversível!",
    UiTextKey.AccountDeleteConfirmMessage to "Todos os dados serão excluídos permanentemente: histórico, banco de palavras, materiais, configurações. Digite sua senha para confirmar",
    UiTextKey.AccountDeletePasswordLabel to "Senha",
    UiTextKey.AccountDeleteButton to "Excluir conta",
    UiTextKey.AccountDeleteSuccess to "Conta excluída com sucesso",
    UiTextKey.AccountDeleteError to "Falha ao excluir",
    UiTextKey.AccountDeleteReauthRequired to "Digite sua senha para confirmar a exclusão",

    // Favorites
    UiTextKey.FavoritesTitle to "Favoritos",
    UiTextKey.FavoritesEmpty to "Nenhum favorito ainda",
    UiTextKey.FavoritesAddSuccess to "Adicionado aos favoritos",
    UiTextKey.FavoritesRemoveSuccess to "Removido dos favoritos",
    UiTextKey.FavoritesAddButton to "Adicionar aos favoritos",
    UiTextKey.FavoritesRemoveButton to "Remover dos favoritos",
    UiTextKey.FavoritesNoteLabel to "Nota",
    UiTextKey.FavoritesNoteHint to "Adicionar nota (opcional)",
    UiTextKey.FavoritesTabRecords to "Registros",
    UiTextKey.FavoritesTabSessions to "Sessões",
    UiTextKey.FavoritesSessionsEmpty to "Nenhuma sessão favorita",
    UiTextKey.FavoritesSessionItemsTemplate to "{count} mensagens",

    // Custom words
    UiTextKey.CustomWordsTitle to "Palavras personalizadas",
    UiTextKey.CustomWordsAdd to "Adicionar palavra",
    UiTextKey.CustomWordsEdit to "Editar palavra",
    UiTextKey.CustomWordsDelete to "Excluir palavra",
    UiTextKey.CustomWordsOriginalLabel to "Original",
    UiTextKey.CustomWordsTranslatedLabel to "Tradução",
    UiTextKey.CustomWordsPronunciationLabel to "Pronúncia (opcional)",
    UiTextKey.CustomWordsExampleLabel to "Exemplo (opcional)",
    UiTextKey.CustomWordsSaveSuccess to "Palavra salva",
    UiTextKey.CustomWordsDeleteSuccess to "Palavra excluída",
    UiTextKey.CustomWordsAlreadyExists to "Palavra já existe",
    UiTextKey.CustomWordsOriginalLanguageLabel to "Idioma original",
    UiTextKey.CustomWordsTranslationLanguageLabel to "Idioma da tradução",
    UiTextKey.CustomWordsSaveButton to "Salvar",
    UiTextKey.CustomWordsCancelButton to "Cancelar",

    // Language detection
    UiTextKey.LanguageDetectAuto to "Detecção automática",
    UiTextKey.LanguageDetectDetecting to "Detectando...",
    UiTextKey.LanguageDetectedTemplate to "Detectado: {language}",
    UiTextKey.LanguageDetectFailed to "Falha na detecção",

    // Image recognition
    UiTextKey.ImageRecognitionButton to "Escanear texto da imagem",
    UiTextKey.ImageRecognitionAccuracyWarning to "⚠️ Aviso: O reconhecimento de texto pode não ser totalmente preciso. Revise o texto detectado. " +
            "Suporta Latim (Inglês, etc.), Chinês, Japonês e Coreano",
    UiTextKey.ImageRecognitionScanning to "Escaneando texto...",
    UiTextKey.ImageRecognitionSuccess to "Texto detectado com sucesso",

    // Cache
    UiTextKey.CacheClearButton to "Limpar cache",
    UiTextKey.CacheClearSuccess to "Cache limpo",
    UiTextKey.CacheStatsTemplate to "Cache: {count} traduções salvas",

    // Auto theme
    UiTextKey.SettingsAutoThemeTitle to "Tema automático",
    UiTextKey.SettingsAutoThemeDesc to "Alternar claro/escuro automaticamente por horário",
    UiTextKey.SettingsAutoThemeEnabled to "Ativado",
    UiTextKey.SettingsAutoThemeDisabled to "Desativado",
    UiTextKey.SettingsAutoThemeDarkStartLabel to "Início do escuro:",
    UiTextKey.SettingsAutoThemeLightStartLabel to "Início do claro:",
    UiTextKey.SettingsAutoThemePreview to "O tema mudará automaticamente conforme o horário definido",

    // Offline mode
    UiTextKey.OfflineModeTitle to "Modo offline",
    UiTextKey.OfflineModeMessage to "Você está offline. Dados em cache serão exibidos",
    UiTextKey.OfflineModeRetry to "Tentar reconectar",
    UiTextKey.OfflineDataCached to "Dados em cache disponíveis",
    UiTextKey.OfflineSyncPending to "Alterações serão sincronizadas quando online",

    // Image capture
    UiTextKey.ImageSourceTitle to "Escolha a fonte da imagem",
    UiTextKey.ImageSourceCamera to "Tirar foto",
    UiTextKey.ImageSourceGallery to "Escolher da galeria",
    UiTextKey.ImageSourceCancel to "Cancelar",
    UiTextKey.CameraCaptureContentDesc to "Tirar foto",

    // Friends
    UiTextKey.FriendsTitle to "Amigos",
    UiTextKey.FriendsMenuButton to "Amigos",
    UiTextKey.FriendsAddButton to "Adicionar amigo",
    UiTextKey.FriendsSearchTitle to "Buscar usuário",
    UiTextKey.FriendsSearchPlaceholder to "Nome de usuário ou ID...",
    UiTextKey.FriendsSearchMinChars to "Digite pelo menos 2 caracteres",
    UiTextKey.FriendsSearchNoResults to "Nenhum usuário encontrado",
    UiTextKey.FriendsListEmpty to "Adicione amigos para conversar e compartilhar materiais",
    UiTextKey.FriendsRequestsSection to "Solicitações ({count})",
    UiTextKey.FriendsSectionTitle to "Amigos ({count})",
    UiTextKey.FriendsAcceptButton to "Aceitar",
    UiTextKey.FriendsRejectButton to "Recusar",
    UiTextKey.FriendsRemoveButton to "Remover",
    UiTextKey.FriendsRemoveDialogTitle to "Remover amigo",
    UiTextKey.FriendsRemoveDialogMessage to "Remover {username} da lista de amigos?",
    UiTextKey.FriendsSendRequestButton to "Adicionar",
    UiTextKey.FriendsRequestSentSuccess to "Solicitação enviada!",
    UiTextKey.FriendsRequestAcceptedSuccess to "Solicitação aceita!",
    UiTextKey.FriendsRequestRejectedSuccess to "Solicitação recusada",
    UiTextKey.FriendsRemovedSuccess to "Amigo removido",
    UiTextKey.FriendsRequestFailed to "Falha ao enviar",
    UiTextKey.FriendsCloseButton to "Fechar",
    UiTextKey.FriendsCancelButton to "Cancelar",
    UiTextKey.FriendsRemoveConfirm to "Remover",
    UiTextKey.FriendsNewRequestsTemplate to "{count} novas solicitações!",
    UiTextKey.FriendsSentRequestsSection to "Enviadas ({count})",
    UiTextKey.FriendsPendingStatus to "Pendente",
    UiTextKey.FriendsCancelRequestButton to "Cancelar solicitação",
    UiTextKey.FriendsUnreadMessageDesc to "Enviar mensagem",
    UiTextKey.FriendsDeleteModeButton to "Excluir amigos",
    UiTextKey.FriendsDeleteSelectedButton to "Excluir selecionados",
    UiTextKey.FriendsDeleteMultipleTitle to "Excluir amigos",
    UiTextKey.FriendsDeleteMultipleMessage to "Excluir {count} amigos selecionados?",
    UiTextKey.FriendsSearchMinChars3 to "Digite pelo menos 3 caracteres para nome",
    UiTextKey.FriendsSearchByUserIdHint to "Ou busque exatamente pelo User ID",
    UiTextKey.FriendsStatusAlreadyFriends to "Já são amigos",
    UiTextKey.FriendsStatusRequestSent to "Enviada — aguardando resposta",
    UiTextKey.FriendsStatusRequestReceived to "Este usuário enviou uma solicitação para você",

    // Chat
    UiTextKey.ChatTitle to "Conversa com {username}",
    UiTextKey.ChatInputPlaceholder to "Digite uma mensagem...",
    UiTextKey.ChatSendButton to "Enviar",
    UiTextKey.ChatEmpty to "Nenhuma mensagem ainda. Inicie a conversa!",
    UiTextKey.ChatMessageSent to "Mensagem enviada",
    UiTextKey.ChatMessageFailed to "Falha ao enviar",
    UiTextKey.ChatMarkingRead to "Lendo...",
    UiTextKey.ChatLoadingMessages to "Carregando mensagens...",
    UiTextKey.ChatToday to "Hoje",
    UiTextKey.ChatYesterday to "Ontem",
    UiTextKey.ChatUnreadBadge to "{count} não lidas",
    UiTextKey.ChatTranslateButton to "Traduzir",
    UiTextKey.ChatTranslateDialogTitle to "Traduzir chat",
    UiTextKey.ChatTranslateDialogMessage to "Traduzir mensagens do amigo para seu idioma? Cada mensagem será detectada e traduzida automaticamente",
    UiTextKey.ChatTranslateConfirm to "Traduzir tudo",
    UiTextKey.ChatTranslating to "Traduzindo mensagens...",
    UiTextKey.ChatTranslated to "Mensagens traduzidas",
    UiTextKey.ChatShowOriginal to "Mostrar original",
    UiTextKey.ChatShowTranslation to "Mostrar tradução",
    UiTextKey.ChatTranslateFailed to "Falha na tradução",
    UiTextKey.ChatTranslatedLabel to "Traduzido",

    // Sharing
    UiTextKey.ShareTitle to "Compartilhar",
    UiTextKey.ShareInboxTitle to "Caixa de compartilhamento",
    UiTextKey.ShareInboxEmpty to "Nenhum compartilhamento ainda. Amigos podem compartilhar palavras e materiais!",
    UiTextKey.ShareWordButton to "Compartilhar palavra",
    UiTextKey.ShareMaterialButton to "Compartilhar material",
    UiTextKey.ShareSelectFriendTitle to "Selecionar amigo",
    UiTextKey.ShareSelectFriendMessage to "Selecione um amigo para compartilhar:",
    UiTextKey.ShareSuccess to "Compartilhado com sucesso!",
    UiTextKey.ShareFailed to "Falha ao compartilhar",
    UiTextKey.ShareWordWith to "Compartilhar palavra com {username}",
    UiTextKey.ShareMaterialWith to "Compartilhar material com {username}",
    UiTextKey.ShareAcceptButton to "Aceitar",
    UiTextKey.ShareDismissButton to "Rejeitar",
    UiTextKey.ShareAccepted to "Adicionado à coleção",
    UiTextKey.ShareDismissed to "Item rejeitado",
    UiTextKey.ShareActionFailed to "Ação falhou",
    UiTextKey.ShareTypeWord to "Palavra",
    UiTextKey.ShareTypeLearningSheet to "Material de aprendizagem",
    UiTextKey.ShareTypeQuiz to "Quiz",
    UiTextKey.ShareReceivedFrom to "De: {username}",
    UiTextKey.ShareNewItemsTemplate to "{count} novos itens!",
    UiTextKey.ShareViewFullMaterial to "Toque em \"Ver\" para visualizar o material completo",
    UiTextKey.ShareDeleteItemTitle to "Excluir item",
    UiTextKey.ShareDeleteItemMessage to "Excluir este item compartilhado? Não pode ser desfeito",
    UiTextKey.ShareDeleteButton to "Excluir",
    UiTextKey.ShareViewButton to "Ver",
    UiTextKey.ShareItemNotFound to "Item não encontrado",
    UiTextKey.ShareNoContent to "Sem conteúdo no material",
    UiTextKey.ShareSaveToSelf to "Salvar na própria caixa",
    UiTextKey.ShareSavedToSelf to "Salvo na sua caixa!",

    // My profile
    UiTextKey.MyProfileTitle to "Meu perfil",
    UiTextKey.MyProfileUserId to "User ID",
    UiTextKey.MyProfileUsername to "Nome de usuário",
    UiTextKey.MyProfileDisplayName to "Nome de exibição",
    UiTextKey.MyProfileCopyUserId to "Copiar ID",
    UiTextKey.MyProfileCopyUsername to "Copiar nome de usuário",
    UiTextKey.MyProfileShare to "Compartilhar perfil",
    UiTextKey.MyProfileCopied to "Copiado!",
    UiTextKey.MyProfileLanguages to "Idiomas",
    UiTextKey.MyProfilePrimaryLanguage to "Idioma principal",
    UiTextKey.MyProfileLearningLanguages to "Idiomas em aprendizagem",

    // Friends info/empty
    UiTextKey.FriendsInfoTitle to "Página de amigos",
    UiTextKey.FriendsInfoMessage to "• Deslize para atualizar lista, solicitações e status\n" +
            "• Toque no card para abrir chat\n" +
            "• Ponto vermelho (●) para mensagens não lidas; ✓✓ para todas lidas\n" +
            "• 📥 para caixa de compartilhamento; ✓✓ para limpar badges\n" +
            "• 🚫 para bloquear — remove amigo e bloqueia interações\n" +
            "• Bloquear também apaga o histórico de chat\n" +
            "• Ícone de lixeira para modo de exclusão\n" +
            "• Remover amigo apaga todas as mensagens\n" +
            "• Ícone de busca para procurar por nome ou ID\n" +
            "• Notificações desativadas por padrão — ative em Configurações\n",
    UiTextKey.FriendsEmptyTitle to "Nenhum amigo ainda",
    UiTextKey.FriendsEmptyMessage to "Toque em \"Adicionar amigo\" para buscar por nome ou ID\n",
    UiTextKey.FriendsInfoGotItButton to "Entendi",

    // Learning info/empty
    UiTextKey.LearningInfoTitle to "Página de aprendizagem",
    UiTextKey.LearningInfoMessage to "• Deslize para atualizar a lista\n" +
            "• Cada card mostra idioma e contagem\n" +
            "• \"Gerar\" para materiais (grátis na primeira vez)\n" +
            "• Regenerar precisa de 5 registros adicionais\n" +
            "• Botão de material para abrir o gerado\n" +
            "• Após gerar material, pode fazer quiz",
    UiTextKey.LearningEmptyTitle to "Nenhum histórico de tradução ainda",
    UiTextKey.LearningEmptyMessage to "Comece a traduzir para criar registros\n" +
            "Materiais são gerados do histórico\n" +
            "Após traduzir, deslize para atualizar",
    UiTextKey.LearningInfoGotItButton to "Entendi",

    // Word bank info/empty
    UiTextKey.WordBankInfoTitle to "Página do banco de palavras",
    UiTextKey.WordBankInfoMessage to "• Deslize para atualizar a lista de idiomas\n" +
            "• Selecione um idioma para ver ou gerar\n" +
            "• Banco de palavras gerado do histórico\n" +
            "• Atualizar precisa de 20 registros adicionais\n" +
            "• Adicione palavras personalizadas manualmente\n" +
            "• Compartilhe palavras com amigos",
    UiTextKey.WordBankInfoGotItButton to "Entendi",

    // Shared inbox info
    UiTextKey.ShareInboxInfoTitle to "Caixa de compartilhamento",
    UiTextKey.ShareInboxInfoMessage to "• Deslize para atualizar a caixa\n" +
            "• Compartilhamentos de amigos aparecem aqui\n" +
            "• Aceite ou rejeite palavras\n" +
            "• \"Ver\" para materiais e quizzes\n" +
            "• Ponto vermelho (●) para itens novos/não lidos\n" +
            "• Confirmação antes de rejeitar palavras compartilhadas",
    UiTextKey.ShareInboxInfoGotItButton to "Entendi",

    // Profile visibility
    UiTextKey.MyProfileVisibilityLabel to "Visibilidade do perfil",
    UiTextKey.MyProfileVisibilityPublic to "Público",
    UiTextKey.MyProfileVisibilityPrivate to "Privado",
    UiTextKey.MyProfileVisibilityDescription to "Público: Qualquer um pode buscar e adicionar você\nPrivado: Não aparece nas buscas",

    // Shared word dismiss
    UiTextKey.ShareDismissWordTitle to "Rejeitar palavra",
    UiTextKey.ShareDismissWordMessage to "Rejeitar palavra compartilhada? Não pode ser desfeito",

    // Shared inbox sheet label
    UiTextKey.ShareLearningSheetLanguageLabel to "Idioma: {language}",

    // Accessibility
    UiTextKey.AccessibilityDismiss to "Dispensar",
    UiTextKey.AccessibilityAlreadyConnectedOrPending to "Conectado ou pendente",
    UiTextKey.AccessibilityNewMessages to "Novas mensagens",
    UiTextKey.AccessibilityNewReleasesIcon to "Indicador de novos itens",
    UiTextKey.AccessibilitySuccessIcon to "Sucesso",
    UiTextKey.AccessibilityErrorIcon to "Erro",
    UiTextKey.AccessibilitySharedItemTypeIcon to "Tipo de item compartilhado",
    UiTextKey.AccessibilityAddCustomWords to "Adicionar palavras personalizadas",
    UiTextKey.AccessibilityWordBankExists to "Banco de palavras existe",

    // Settings hardcoded
    UiTextKey.SettingsTesterFeedback to "PT-BR.Feedback",

    // Friends notification settings
    UiTextKey.FriendsNotifSettingsButton to "Configurações de notificação",
    UiTextKey.FriendsNotifSettingsTitle to "Configurações de notificação",
    UiTextKey.FriendsNotifNewMessages to "Novas mensagens de chat",
    UiTextKey.FriendsNotifFriendRequests to "Solicitações aceitas",
    UiTextKey.FriendsNotifRequestAccepted to "Solicitações aprovadas",
    UiTextKey.FriendsNotifSharedInbox to "Novos itens compartilhados",
    UiTextKey.FriendsNotifCloseButton to "Concluído",

    // In-app badge settings
    UiTextKey.InAppBadgeSectionTitle to "Badges no app (pontos vermelhos)",
    UiTextKey.InAppBadgeMessages to "Badge de mensagens não lidas",
    UiTextKey.InAppBadgeFriendRequests to "Badge de solicitações de amizade",
    UiTextKey.InAppBadgeSharedInbox to "Badge da caixa de compartilhamento",

    // Error messages
    UiTextKey.ErrorNotLoggedIn to "Entre para continuar",
    UiTextKey.ErrorSaveFailedRetry to "Falha ao salvar. Tente novamente",
    UiTextKey.ErrorLoadFailedRetry to "Falha ao carregar. Tente novamente",
    UiTextKey.ErrorNetworkRetry to "Erro de rede. Verifique a conexão",

    // Learning progress
    UiTextKey.LearningProgressNeededTemplate to "Precisa de mais {needed} traduções para gerar material",

    // Speech shortcut
    UiTextKey.SpeechSwitchToConversation to "Mudar para conversa contínua →",

    // Chat clear
    UiTextKey.ChatClearConversationButton to "Limpar conversa",
    UiTextKey.ChatClearConversationTitle to "Limpar conversa",
    UiTextKey.ChatClearConversationMessage to "Ocultar todas as mensagens? Permanecerão ocultas ao reabrir. Não afeta a outra pessoa",
    UiTextKey.ChatClearConversationConfirm to "Limpar tudo",
    UiTextKey.ChatClearConversationSuccess to "Conversa limpa",

    // Block user
    UiTextKey.BlockUserButton to "Bloquear",
    UiTextKey.BlockUserTitle to "Bloquear este usuário?",
    UiTextKey.BlockUserMessage to "Bloquear {username}? Será removido da lista e bloqueado de interagir",
    UiTextKey.BlockUserConfirm to "Bloquear",
    UiTextKey.BlockUserSuccess to "Bloqueado e removido da lista",
    UiTextKey.BlockedUsersTitle to "Usuários bloqueados",
    UiTextKey.BlockedUsersEmpty to "Nenhum usuário bloqueado",
    UiTextKey.UnblockUserButton to "Desbloquear",
    UiTextKey.UnblockUserTitle to "Desbloquear?",
    UiTextKey.UnblockUserMessage to "Desbloquear {username}? Poderão enviar solicitações novamente",
    UiTextKey.UnblockUserSuccess to "Desbloqueado",
    UiTextKey.BlockedUsersManageButton to "Gerenciar bloqueados",

    // Friend request note
    UiTextKey.FriendsRequestNoteLabel to "Nota da solicitação (opcional)",
    UiTextKey.FriendsRequestNotePlaceholder to "Adicionar nota breve...",

    // Generation banners
    UiTextKey.GenerationBannerSheet to "Material pronto! Toque para abrir",
    UiTextKey.GenerationBannerWordBank to "Banco de palavras pronto! Toque para ver",
    UiTextKey.GenerationBannerQuiz to "Quiz pronto! Toque para começar",

    // Notification settings quick link
    UiTextKey.NotifSettingsQuickLink to "Notificações",

    // Language name for Traditional Chinese
    UiTextKey.LangZhTw to "Chinês (Tradicional)",

    // Help extra sections
    UiTextKey.HelpFriendSystemTitle to "Sistema de amigos",
    UiTextKey.HelpFriendSystemBody to "• Busque amigos por nome ou ID\n" +
            "• Envie, aceite ou recuse solicitações\n" +
            "• Converse diretamente com tradução\n" +
            "• Compartilhe palavras e materiais de aprendizagem\n" +
            "• Gerencie conteúdo compartilhado na caixa\n" +
            "• Ponto vermelho (●) para conteúdo novo/não lido\n" +
            "• Deslize para baixo para atualizar",
    UiTextKey.HelpProfileVisibilityTitle to "Visibilidade do perfil",
    UiTextKey.HelpProfileVisibilityBody to "• Configure perfil como público ou privado em Configurações\n" +
            "• Público: Qualquer um pode buscar você\n" +
            "• Privado: Não aparece nas buscas\n" +
            "• Use privado: compartilhe ID para ser adicionado",
    UiTextKey.HelpColorPalettesTitle to "Temas e moedas",
    UiTextKey.HelpColorPalettesBody to "• 1 tema grátis: Sky Blue (padrão)\n" +
            "• 10 temas desbloqueáveis por 10 moedas cada\n" +
            "• Ganhe moedas nos quizzes\n" +
            "• Use moedas para temas e expansão de histórico\n" +
            "• Tema automático: claro 6-18, escuro 18-6",
    UiTextKey.HelpPrivacyTitle to "Privacidade e dados",
    UiTextKey.HelpPrivacyBody to "• Voz usada apenas para reconhecimento, não armazenada permanentemente\n" +
            "• OCR processado no dispositivo (seguro)\n" +
            "• Pode excluir conta e dados a qualquer momento\n" +
            "• Modo privado: não aparece nas buscas\n" +
            "• Todos os dados sincronizados com segurança no Firebase",
    UiTextKey.HelpAppVersionTitle to "Versão do app",
    UiTextKey.HelpAppVersionNotes to "• Limite de histórico: 30 a 60 registros (expanda com moedas)\n" +
            "• Nomes de usuário únicos — mude para liberar o antigo\n" +
            "• Logout automático em atualizações de segurança\n" +
            "• Todas as traduções via Azure AI",

    // Onboarding
    UiTextKey.OnboardingPage1Title to "Tradução Instantânea",
    UiTextKey.OnboardingPage1Desc to "Tradução rápida para frases curtas. Conversa contínua para discussões bilaterais",
    UiTextKey.OnboardingPage2Title to "Aprenda vocabulário",
    UiTextKey.OnboardingPage2Desc to "Crie listas de palavras e quizzes do histórico",
    UiTextKey.OnboardingPage3Title to "Conecte-se com amigos",
    UiTextKey.OnboardingPage3Desc to "Converse, compartilhe palavras e aprenda juntos",
    UiTextKey.OnboardingSkipButton to "Pular",
    UiTextKey.OnboardingNextButton to "Próximo",
    UiTextKey.OnboardingGetStartedButton to "Começar",

    // Home welcome
    UiTextKey.HomeWelcomeBack to "👋 Bem-vindo de volta, {name}!",

    // Chat labels
    UiTextKey.ChatUsernameLabel to "Usuário:",
    UiTextKey.ChatUserIdLabel to "User ID:",
    UiTextKey.ChatLearningLabel to "Aprendendo:",
    UiTextKey.ChatBlockedMessage to "Não é possível enviar mensagem para este usuário",

    // Custom word bank
    UiTextKey.CustomWordsSearchPlaceholder to "Buscar",
    UiTextKey.CustomWordsEmptyState to "Nenhuma palavra personalizada ainda",
    UiTextKey.CustomWordsEmptyHint to "Toque em + para adicionar palavra",
    UiTextKey.CustomWordsNoSearchResults to "Nenhuma palavra encontrada",
    UiTextKey.AddCustomWordHintTemplate to "Digite a palavra em {from} e a tradução em {to}",

    // Word bank records count
    UiTextKey.WordBankRecordsCountTemplate to "{count} registros",

    // Blocked user ID
    UiTextKey.BlockedUserIdTemplate to "ID: {id}…",

    // Profile extra
    UiTextKey.ProfileEmailTemplate to "E-mail: {email}",
    UiTextKey.ProfileUsernameHintFull to "Nome de usuário para amigos (3–20 caracteres, letras/números/_)",

    // Voice settings
    UiTextKey.VoiceSettingsNoOptions to "Sem opções de voz para este idioma",

    // Login
    UiTextKey.AuthUpdatedLoginAgain to "App atualizado. Por favor entre novamente",

    // Favorites limit/info
    UiTextKey.FavoritesLimitTitle to "Limite de favoritos atingido",
    UiTextKey.FavoritesLimitMessage to "Máximo de 20 favoritos. Exclua outros para adicionar",
    UiTextKey.FavoritesLimitGotIt to "Entendi",
    UiTextKey.FavoritesInfoTitle to "Informações de favoritos",
    UiTextKey.FavoritesInfoMessage to "Máximo de 20 favoritos (registros e sessões). Limitado para carga do banco de dados. Exclua para adicionar novos",
    UiTextKey.FavoritesInfoGotIt to "Entendi",

    // Primary language cooldown
    UiTextKey.SettingsPrimaryLanguageCooldownTitle to "Não pode ser alterado",
    UiTextKey.SettingsPrimaryLanguageCooldownMessage to "O idioma principal pode ser alterado a cada 30 dias. Faltam {days} dias",
    UiTextKey.SettingsPrimaryLanguageCooldownMessageHours to "O idioma principal pode ser alterado a cada 30 dias. Faltam {hours} horas",
    UiTextKey.SettingsPrimaryLanguageConfirmTitle to "Confirmar alteração de idioma",
    UiTextKey.SettingsPrimaryLanguageConfirmMessage to "Alterar o idioma principal não pode ser revertido por 30 dias. Continuar?",

    // Bottom navigation
    UiTextKey.NavHome to "Início",
    UiTextKey.NavTranslate to "Traduzir",
    UiTextKey.NavLearn to "Aprender",
    UiTextKey.NavFriends to "Amigos",
    UiTextKey.NavSettings to "Config.",

    // Permissions
    UiTextKey.CameraPermissionTitle to "Permissão de câmera necessária",
    UiTextKey.CameraPermissionMessage to "Permita o acesso à câmera para reconhecimento de texto",
    UiTextKey.CameraPermissionGrant to "Permitir",
    UiTextKey.MicPermissionMessage to "Permissão de microfone necessária para reconhecimento de voz",

    // Delete confirmations
    UiTextKey.FavoritesDeleteConfirm to "Excluir {count} itens selecionados? Não pode ser desfeito",
    UiTextKey.WordBankDeleteConfirm to "Excluir \"{word}\"?",

    // Friends extras
    UiTextKey.FriendsAcceptAllButton to "Aceitar todos",
    UiTextKey.FriendsRejectAllButton to "Recusar todos",
    UiTextKey.ChatBlockedCannotSend to "Não é possível enviar mensagem",

    // Shop / Unlock
    UiTextKey.ShopUnlockConfirmTitle to "Desbloquear {name}?",
    UiTextKey.ShopUnlockCost to "Custo: {cost} moedas",
    UiTextKey.ShopYourCoins to "Minhas moedas: {coins}",
    UiTextKey.ShopUnlockButton to "Desbloquear",

    // Help: Primary Language Info
    UiTextKey.HelpPrimaryLanguageTitle to "Idioma principal",
    UiTextKey.HelpPrimaryLanguageBody to "• O idioma principal é usado para descrições na aprendizagem\n" +
            "• Pode ser alterado a cada 30 dias para consistência\n" +
            "• Altere em Configurações\n" +
            "• A configuração é aplicada em todas as páginas",

    // Camera language hint
    UiTextKey.CameraLanguageHint to "💡 Dica: Para melhor reconhecimento, defina \"Idioma Fonte\" para o idioma do texto escaneado",

    // Username change cooldown
    UiTextKey.SettingsUsernameCooldownTitle to "Não pode ser alterado",
    UiTextKey.SettingsUsernameCooldownMessage to "O nome de usuário pode ser alterado a cada 30 dias. Faltam {days} dias",
    UiTextKey.SettingsUsernameCooldownMessageHours to "O nome de usuário pode ser alterado a cada 30 dias. Faltam {hours} horas",
    UiTextKey.SettingsUsernameConfirmTitle to "Confirmar alteração de nome de usuário",
    UiTextKey.SettingsUsernameConfirmMessage to "Alterar o nome de usuário não pode ser revertido por 30 dias. Continuar?",

    // Extended Error Messages
    UiTextKey.ErrorNoInternet to "Sem conexão com a internet. Verifique sua conexão",
    UiTextKey.ErrorPermissionDenied to "Sem permissão para esta ação",
    UiTextKey.ErrorSessionExpired to "Sessão expirada. Entre novamente",
    UiTextKey.ErrorItemNotFound to "Item não encontrado. Pode ter sido excluído",
    UiTextKey.ErrorAccessDenied to "Acesso negado",
    UiTextKey.ErrorAlreadyFriends to "Já são amigos",
    UiTextKey.ErrorUserBlocked to "Não permitido. Usuário pode estar bloqueado",
    UiTextKey.ErrorRequestNotFound to "Solicitação não existe mais",
    UiTextKey.ErrorRequestAlreadyHandled to "Solicitação já foi tratada",
    UiTextKey.ErrorNotAuthorized to "Sem permissão para fazer isso",
    UiTextKey.ErrorRateLimited to "Muitas solicitações. Tente novamente depois",
    UiTextKey.ErrorInvalidInput to "Entrada inválida. Verifique e tente novamente",
    UiTextKey.ErrorOperationNotAllowed to "Esta operação não é permitida no momento",
    UiTextKey.ErrorTimeout to "Tempo esgotado. Tente novamente",
    UiTextKey.ErrorSendMessageFailed to "Falha ao enviar mensagem. Tente novamente",
    UiTextKey.ErrorFriendRequestSent to "Solicitação já enviada!",
    UiTextKey.ErrorFriendRequestFailed to "Falha ao enviar solicitação",
    UiTextKey.ErrorFriendRemoved to "Amigo removido",
    UiTextKey.ErrorFriendRemoveFailed to "Falha ao remover. Verifique a conexão",
    UiTextKey.ErrorBlockSuccess to "Usuário bloqueado",
    UiTextKey.ErrorBlockFailed to "Falha ao bloquear. Tente novamente",
    UiTextKey.ErrorUnblockSuccess to "Desbloqueado",
    UiTextKey.ErrorUnblockFailed to "Falha ao desbloquear. Tente novamente",
    UiTextKey.ErrorAcceptRequestSuccess to "Solicitação aceita!",
    UiTextKey.ErrorAcceptRequestFailed to "Falha ao aceitar. Tente novamente",
    UiTextKey.ErrorRejectRequestSuccess to "Solicitação recusada",
    UiTextKey.ErrorRejectRequestFailed to "Falha ao recusar. Tente novamente",
    UiTextKey.ErrorOfflineMessage to "Você está offline. Alguns recursos podem estar indisponíveis",
    UiTextKey.ErrorChatDeletionFailed to "Falha ao limpar conversa. Tente novamente",
    UiTextKey.ErrorGenericRetry to "Ocorreu um erro. Tente novamente",
)
