"""
Ejercicio 1 - Presentación 07
Analizador Sintáctico Descendente Recursivo (ASDR)

Gramática transformada (sin recursividad por la izquierda):
  S  → A B' C    |  S  → D E
  A  → dos B' tres  |  A  → ε
  B' → cuatro C cinco B'  |  B' → ε
  C  → seis A B'  |  C  → ε
  D  → uno A E    |  D  → B'
  E  → tres

NOTA: La gramática NO es LL(1) debido a conflicto en S con 'cuatro'.
      Se resuelve eligiendo S → A B' C cuando token es 'cuatro'.
"""

# Tipos de tokens
UNO, DOS, TRES, CUATRO, CINCO, SEIS, FIN = "uno", "dos", "tres", "cuatro", "cinco", "seis", "$"


class ASDR:
    def __init__(self, cadena: list[str]):
        self.tokens = cadena + [FIN]
        self.pos = 0
        self.token_actual = self.tokens[0]
        self.traza = []
        self.cadena_str = " ".join(cadena) if cadena else "ε (vacía)"

    # ─────────────────────────────────────────────
    # Función principal
    # ─────────────────────────────────────────────
    def analizar(self) -> bool:
        try:
            self.traza.append(f"Cadena de entrada: {self.cadena_str}")
            self.traza.append("─" * 50)
            self.S()
            if self.token_actual != FIN:
                self.error_sintaxis(FIN)
            self.traza.append("─" * 50)
            self.traza.append("✓ Análisis EXITOSO: la cadena pertenece al lenguaje.")
            return True
        except SyntaxError as e:
            self.traza.append("─" * 50)
            self.traza.append(f"✗ Error sintáctico: {e}")
            return False

    # ─────────────────────────────────────────────
    # Funciones para cada no terminal
    # ─────────────────────────────────────────────

    def S(self):
        self.traza.append(f"  S()  →  token actual: '{self.token_actual}'")
        # PRED(S → A B' C) = { dos, cuatro, seis, $ }
        # PRED(S → D E)    = { uno, cuatro, tres }
        # Conflicto en 'cuatro': elegimos S → A B' C
        if self.token_actual in (DOS, SEIS, FIN, CUATRO):
            self.traza.append("    ↳ Regla: S → A B' C")
            self.A()
            self.Bprima()
            self.C()
        elif self.token_actual in (UNO, TRES):
            self.traza.append("    ↳ Regla: S → D E")
            self.D()
            self.E()
        else:
            self.error_sintaxis(DOS, CUATRO, SEIS, UNO, TRES, FIN)

    def A(self):
        self.traza.append(f"  A()  →  token actual: '{self.token_actual}'")
        if self.token_actual == DOS:
            self.traza.append("    ↳ Regla: A → dos B' tres")
            self.emparejar(DOS)
            self.Bprima()
            self.emparejar(TRES)
        else:
            # PRED(A → ε) = { cuatro, seis, cinco, tres, $ }
            self.traza.append("    ↳ Regla: A → ε")

    def Bprima(self):
        self.traza.append(f"  B'() →  token actual: '{self.token_actual}'")
        if self.token_actual == CUATRO:
            self.traza.append("    ↳ Regla: B' → cuatro C cinco B'")
            self.emparejar(CUATRO)
            self.C()
            self.emparejar(CINCO)
            self.Bprima()
        else:
            # PRED(B' → ε) = { seis, tres, cinco, $ }
            self.traza.append("    ↳ Regla: B' → ε")

    def C(self):
        self.traza.append(f"  C()  →  token actual: '{self.token_actual}'")
        if self.token_actual == SEIS:
            self.traza.append("    ↳ Regla: C → seis A B'")
            self.emparejar(SEIS)
            self.A()
            self.Bprima()
        else:
            # PRED(C → ε) = { cinco, $ }
            self.traza.append("    ↳ Regla: C → ε")

    def D(self):
        self.traza.append(f"  D()  →  token actual: '{self.token_actual}'")
        if self.token_actual == UNO:
            self.traza.append("    ↳ Regla: D → uno A E")
            self.emparejar(UNO)
            self.A()
            self.E()
        else:
            # PRED(D → B') = { cuatro, tres }
            self.traza.append("    ↳ Regla: D → B'")
            self.Bprima()

    def E(self):
        self.traza.append(f"  E()  →  token actual: '{self.token_actual}'")
        self.traza.append("    ↳ Regla: E → tres")
        self.emparejar(TRES)

    # ─────────────────────────────────────────────
    # Emparejar terminal
    # ─────────────────────────────────────────────
    def emparejar(self, esperado: str):
        if self.token_actual == esperado:
            self.traza.append(f"    ✓ emparejar('{esperado}') — OK")
            self.pos += 1
            self.token_actual = self.tokens[self.pos] if self.pos < len(self.tokens) else FIN
        else:
            self.error_sintaxis(esperado)

    # ─────────────────────────────────────────────
    # Error
    # ─────────────────────────────────────────────
    def error_sintaxis(self, *esperados):
        esp = " o ".join(f"'{e}'" for e in esperados)
        msg = f"Se esperaba {esp} pero se encontró '{self.token_actual}' (posición {self.pos})"
        raise SyntaxError(msg)

    # ─────────────────────────────────────────────
    # Imprimir traza
    # ─────────────────────────────────────────────
    def imprimir_traza(self):
        for linea in self.traza:
            print(f"  {linea}")
        print()


# ═════════════════════════════════════════════════
# EJECUCIÓN DE PRUEBAS
# ═════════════════════════════════════════════════

def main():
    print()
    print("╔" + "═" * 62 + "╗")
    print("║       ASDR — Ejercicio 1, Presentación 07                  ║")
    print("║       Analizador Sintáctico Descendente Recursivo          ║")
    print("╠" + "═" * 62 + "╣")
    print("║  Gramática transformada:                                   ║")
    print("║    S  → A B' C        |  S  → D E                         ║")
    print("║    A  → dos B' tres   |  A  → ε                           ║")
    print("║    B' → cuatro C cinco B'  |  B' → ε                      ║")
    print("║    C  → seis A B'     |  C  → ε                           ║")
    print("║    D  → uno A E       |  D  → B'                          ║")
    print("║    E  → tres                                              ║")
    print("╚" + "═" * 62 + "╝")
    print()

    # Definir casos de prueba
    casos = [
        # (cadena, descripción)
        ([],                                                     "ε (cadena vacía)"),
        (["dos", "tres"],                                        "dos tres"),
        (["cuatro", "cinco"],                                    "cuatro cinco"),
        (["seis"],                                               "seis"),
        (["uno", "tres", "tres"],                                "uno tres tres"),
        (["dos", "cuatro", "seis", "cinco", "tres"],             "dos cuatro seis cinco tres"),
        (["tres"],                                               "tres"),
        (["uno", "dos", "tres", "tres", "tres"],                 "uno dos tres tres tres"),
        (["cuatro", "seis", "cinco", "cinco"],                   "cuatro seis cinco cinco"),
        # Casos de ERROR
        (["uno", "dos"],                                         "uno dos (ERROR esperado)"),
        (["cinco"],                                              "cinco (ERROR esperado)"),
        (["dos", "cuatro"],                                      "dos cuatro (ERROR esperado)"),
    ]

    resultados = []

    for i, (cadena, desc) in enumerate(casos, 1):
        print("═" * 64)
        print(f"  CASO {i}: {desc}")
        print("═" * 64)

        asdr = ASDR(cadena)
        exito = asdr.analizar()
        asdr.imprimir_traza()
        resultados.append((desc, exito))

    # ─────────────────────────────────────────────
    # Resumen
    # ─────────────────────────────────────────────
    print()
    print("╔" + "═" * 62 + "╗")
    print("║                   RESUMEN DE RESULTADOS                    ║")
    print("╠" + "═" * 62 + "╣")

    for i, (desc, exito) in enumerate(resultados, 1):
        estado = "✓ ACEPTADA " if exito else "✗ RECHAZADA"
        print(f"║  Caso {i:2d}: {desc:38s} {estado}  ║")

    print("╚" + "═" * 62 + "╝")
    print()

    aceptadas = sum(1 for _, e in resultados if e)
    rechazadas = len(resultados) - aceptadas
    print(f"  Total: {aceptadas} aceptadas, {rechazadas} rechazadas de {len(resultados)} casos.")
    print()


if __name__ == "__main__":
    main()
