# TODO fRankup

- Recompensas por rank, prestígio, `/rankup max`, reset.
- Com multiplicador 2.0 o último rankup custa ~8,6×10^14 €: afinar `multiplier` no `config.yml`.
- `pending` do `RankService` nunca é limpo ao sair o jogador.
- Confirmação por clique: na mensagem de confirmação do `/rankup` (e no menu `/rank`) um "aqui" clicável confirma, em vez de repetir o comando.

- Reload de `base`/`multiplier` exige `/freload plugin` (`RankCosts` é fixo no `RankService`); as mensagens já se releem com `/freload config`.
