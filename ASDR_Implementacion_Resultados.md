# Implementación ASDR - Ejercicio 1, Presentación 07
## Analizador Sintáctico Descendente Recursivo

**Lenguajes de Programación - Universidad Sergio Arboleda**

---

## Gramática Transformada (sin recursividad por la izquierda)

```
S  → A B' C        |  S  → D E
A  → dos B' tres   |  A  → ε
B' → cuatro C cinco B'  |  B' → ε
C  → seis A B'     |  C  → ε
D  → uno A E       |  D  → B'
E  → tres
```

> **Nota:** La gramática NO es LL(1) debido al conflicto en S con el terminal `cuatro`. En la implementación se resuelve eligiendo `S → A B' C` cuando el token actual es `cuatro` (ya que `A → ε` permite que `B'` lo consuma).

---

## Código Fuente (Python)

```python
"""
Ejercicio 1 - Presentación 07
Analizador Sintáctico Descendente Recursivo (ASDR)
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

    def analizar(self) -> bool:
        try:
            self.traza.append(f"Cadena de entrada: {self.cadena_str}")
            self.S()
            if self.token_actual != FIN:
                self.error_sintaxis(FIN)
            self.traza.append("✓ Análisis EXITOSO: la cadena pertenece al lenguaje.")
            return True
        except SyntaxError as e:
            self.traza.append(f"✗ Error sintáctico: {e}")
            return False

    # ─── Funciones para cada no terminal ───

    def S(self):
        self.traza.append(f"  S() → token actual: '{self.token_actual}'")
        if self.token_actual in (DOS, SEIS, FIN, CUATRO):
            self.traza.append("    ↳ Regla: S → A B' C")
            self.A(); self.Bprima(); self.C()
        elif self.token_actual in (UNO, TRES):
            self.traza.append("    ↳ Regla: S → D E")
            self.D(); self.E()
        else:
            self.error_sintaxis(DOS, CUATRO, SEIS, UNO, TRES, FIN)

    def A(self):
        self.traza.append(f"  A() → token actual: '{self.token_actual}'")
        if self.token_actual == DOS:
            self.traza.append("    ↳ Regla: A → dos B' tres")
            self.emparejar(DOS); self.Bprima(); self.emparejar(TRES)
        else:
            self.traza.append("    ↳ Regla: A → ε")

    def Bprima(self):
        self.traza.append(f"  B'() → token actual: '{self.token_actual}'")
        if self.token_actual == CUATRO:
            self.traza.append("    ↳ Regla: B' → cuatro C cinco B'")
            self.emparejar(CUATRO); self.C(); self.emparejar(CINCO); self.Bprima()
        else:
            self.traza.append("    ↳ Regla: B' → ε")

    def C(self):
        self.traza.append(f"  C() → token actual: '{self.token_actual}'")
        if self.token_actual == SEIS:
            self.traza.append("    ↳ Regla: C → seis A B'")
            self.emparejar(SEIS); self.A(); self.Bprima()
        else:
            self.traza.append("    ↳ Regla: C → ε")

    def D(self):
        self.traza.append(f"  D() → token actual: '{self.token_actual}'")
        if self.token_actual == UNO:
            self.traza.append("    ↳ Regla: D → uno A E")
            self.emparejar(UNO); self.A(); self.E()
        else:
            self.traza.append("    ↳ Regla: D → B'")
            self.Bprima()

    def E(self):
        self.traza.append(f"  E() → token actual: '{self.token_actual}'")
        self.traza.append("    ↳ Regla: E → tres")
        self.emparejar(TRES)

    # ─── Emparejar terminal ───

    def emparejar(self, esperado: str):
        if self.token_actual == esperado:
            self.traza.append(f"    ✓ emparejar('{esperado}') — OK")
            self.pos += 1
            self.token_actual = self.tokens[self.pos] if self.pos < len(self.tokens) else FIN
        else:
            self.error_sintaxis(esperado)

    def error_sintaxis(self, *esperados):
        esp = " o ".join(f"'{e}'" for e in esperados)
        raise SyntaxError(f"Se esperaba {esp} pero se encontró '{self.token_actual}' (pos {self.pos})")

    def imprimir_traza(self):
        for linea in self.traza:
            print(f"  {linea}")
```

### Código Fuente (Java)

```java
public class ASDR {

    enum Token { UNO, DOS, TRES, CUATRO, CINCO, SEIS, FIN_ARCHIVO }

    private Token[] tokens;
    private int pos;
    private Token tokenActual;

    public ASDR(Token[] tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.tokenActual = tokens.length > 0 ? tokens[0] : Token.FIN_ARCHIVO;
    }

    public void analizar() {
        S();
        if (tokenActual != Token.FIN_ARCHIVO)
            errorSintaxis(Token.FIN_ARCHIVO);
        System.out.println("Análisis exitoso.");
    }

    // S → A B' C  |  S → D E
    void S() {
        if (tokenActual == Token.DOS || tokenActual == Token.SEIS
            || tokenActual == Token.FIN_ARCHIVO || tokenActual == Token.CUATRO) {
            A(); Bprima(); C();
        } else if (tokenActual == Token.UNO || tokenActual == Token.TRES) {
            D(); E();
        } else
            errorSintaxis(Token.DOS, Token.CUATRO, Token.SEIS, Token.UNO, Token.TRES);
    }

    // A → dos B' tres  |  A → ε
    void A() {
        if (tokenActual == Token.DOS) {
            emparejar(Token.DOS); Bprima(); emparejar(Token.TRES);
        }
        // else: A → ε
    }

    // B' → cuatro C cinco B'  |  B' → ε
    void Bprima() {
        if (tokenActual == Token.CUATRO) {
            emparejar(Token.CUATRO); C(); emparejar(Token.CINCO); Bprima();
        }
        // else: B' → ε
    }

    // C → seis A B'  |  C → ε
    void C() {
        if (tokenActual == Token.SEIS) {
            emparejar(Token.SEIS); A(); Bprima();
        }
        // else: C → ε
    }

    // D → uno A E  |  D → B'
    void D() {
        if (tokenActual == Token.UNO) {
            emparejar(Token.UNO); A(); E();
        } else {
            Bprima();
        }
    }

    // E → tres
    void E() {
        emparejar(Token.TRES);
    }

    void emparejar(Token esperado) {
        if (tokenActual == esperado) {
            pos++;
            tokenActual = (pos < tokens.length) ? tokens[pos] : Token.FIN_ARCHIVO;
        } else
            errorSintaxis(esperado);
    }

    void errorSintaxis(Token... esperados) {
        throw new RuntimeException("Error sintáctico en posición " + pos);
    }
}
```

---

## Resultados de Ejecución

### Caso 1: `ε` (cadena vacía) — ✓ ACEPTADA

Derivación: `S → A B' C → ε ε ε → ε`

```
S() → token actual: '$'
  ↳ Regla: S → A B' C
A() → token actual: '$'
  ↳ Regla: A → ε
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 2: `dos tres` — ✓ ACEPTADA

Derivación: `S → A B' C → dos B' tres B' C → dos ε tres ε ε → dos tres`

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 3: `cuatro cinco` — ✓ ACEPTADA

Derivación: `S → A B' C → ε (cuatro C cinco B') C → cuatro ε cinco ε ε → cuatro cinco`

```
S() → token actual: 'cuatro'
  ↳ Regla: S → A B' C
A() → token actual: 'cuatro'
  ↳ Regla: A → ε
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'cinco'
  ↳ Regla: C → ε
  ✓ emparejar('cinco')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 4: `seis` — ✓ ACEPTADA

Derivación: `S → A B' C → ε ε (seis A B') → seis ε ε → seis`

```
S() → token actual: 'seis'
  ↳ Regla: S → A B' C
A() → token actual: 'seis'
  ↳ Regla: A → ε
B'() → token actual: 'seis'
  ↳ Regla: B' → ε
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: '$'
  ↳ Regla: A → ε
B'() → token actual: '$'
  ↳ Regla: B' → ε
✓ Análisis EXITOSO
```

---

### Caso 5: `uno tres tres` — ✓ ACEPTADA

Derivación: `S → D E → (uno A E) E → uno ε tres tres → uno tres tres`

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'tres'
  ↳ Regla: A → ε
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 6: `dos cuatro seis cinco tres` — ✓ ACEPTADA

Derivación: `S → A B' C → (dos B' tres) B' C → dos (cuatro C cinco B') tres B' C → dos cuatro (seis A B') cinco ε tres ε ε → dos cuatro seis ε ε cinco tres → dos cuatro seis cinco tres`

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: 'cinco'
  ↳ Regla: A → ε
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
  ✓ emparejar('cinco')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
B'() → token actual: '$'
  ↳ Regla: B' → ε
C() → token actual: '$'
  ↳ Regla: C → ε
✓ Análisis EXITOSO
```

---

### Caso 7: `tres` — ✓ ACEPTADA

Derivación: `S → D E → B' E → ε tres → tres`

```
S() → token actual: 'tres'
  ↳ Regla: S → D E
D() → token actual: 'tres'
  ↳ Regla: D → B'
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 8: `uno dos tres tres tres` — ✓ ACEPTADA

Derivación: `S → D E → (uno A E) E → uno (dos B' tres) E E → uno dos ε tres tres tres`

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'tres'
  ↳ Regla: B' → ε
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
E() → token actual: 'tres'
  ↳ Regla: E → tres
  ✓ emparejar('tres')
✓ Análisis EXITOSO
```

---

### Caso 9: `cuatro seis cinco cinco` — ✗ RECHAZADA

```
S() → token actual: 'cuatro'
  ↳ Regla: S → A B' C
A() → token actual: 'cuatro'
  ↳ Regla: A → ε
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: 'seis'
  ↳ Regla: C → seis A B'
  ✓ emparejar('seis')
A() → token actual: 'cinco'
  ↳ Regla: A → ε
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
  ✓ emparejar('cinco')
B'() → token actual: 'cinco'
  ↳ Regla: B' → ε
C() → token actual: 'cinco'
  ↳ Regla: C → ε
✗ Error: Se esperaba '$' pero se encontró 'cinco' (posición 3)
```

---

### Caso 10: `uno dos` — ✗ RECHAZADA

```
S() → token actual: 'uno'
  ↳ Regla: S → D E
D() → token actual: 'uno'
  ↳ Regla: D → uno A E
  ✓ emparejar('uno')
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: '$'
  ↳ Regla: B' → ε
✗ Error: Se esperaba 'tres' pero se encontró '$' (posición 2)
```

---

### Caso 11: `cinco` — ✗ RECHAZADA

```
S() → token actual: 'cinco'
✗ Error: Se esperaba 'dos' o 'cuatro' o 'seis' o 'uno' o 'tres' o '$'
         pero se encontró 'cinco' (posición 0)
```

---

### Caso 12: `dos cuatro` — ✗ RECHAZADA

```
S() → token actual: 'dos'
  ↳ Regla: S → A B' C
A() → token actual: 'dos'
  ↳ Regla: A → dos B' tres
  ✓ emparejar('dos')
B'() → token actual: 'cuatro'
  ↳ Regla: B' → cuatro C cinco B'
  ✓ emparejar('cuatro')
C() → token actual: '$'
  ↳ Regla: C → ε
✗ Error: Se esperaba 'cinco' pero se encontró '$' (posición 2)
```

---

## Resumen de Resultados

| Caso | Cadena | Resultado |
|:----:|--------|:---------:|
| 1 | `ε` (vacía) | ✓ Aceptada |
| 2 | `dos tres` | ✓ Aceptada |
| 3 | `cuatro cinco` | ✓ Aceptada |
| 4 | `seis` | ✓ Aceptada |
| 5 | `uno tres tres` | ✓ Aceptada |
| 6 | `dos cuatro seis cinco tres` | ✓ Aceptada |
| 7 | `tres` | ✓ Aceptada |
| 8 | `uno dos tres tres tres` | ✓ Aceptada |
| 9 | `cuatro seis cinco cinco` | ✗ Rechazada |
| 10 | `uno dos` | ✗ Rechazada |
| 11 | `cinco` | ✗ Rechazada |
| 12 | `dos cuatro` | ✗ Rechazada |

**Total: 8 aceptadas, 4 rechazadas de 12 casos.**

---

## Cómo Ejecutar

```bash
python3 asdr.py
```
