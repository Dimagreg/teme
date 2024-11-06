dosseg

; CMMDC pentru A si B

.model small

.stack 100h

.data
    A DB 0
    B DB 0
    n1 DB "Introduceti A: $"
    n2 DB "Introduceti B: $"
    n3 DB "CMMDC: $"

.code

new_line proc
    MOV AH, 2
    MOV DL, 10
    INT 21h;

    RET
new_line endp

print_num proc
    MOV AH, 2
    MOV DL, AL
    ADD DL, '0'
    INT 21h
    RET
print_num endp

main proc
    MOV AX, @data
    MOV DS, AX

    ; read A
    MOV AH, 9
    MOV DX, OFFSET n1
    INT 21h

    MOV AH, 1
    INT 21h

    ADD AL, '0'
    MOV A, AL
    CALL new_line

    ; read B
    MOV AH, 9
    MOV DX, OFFSET n2
    INT 21h

    MOV AH, 1
    INT 21H

    ADD AL, '0'
    MOV B, AL
    CALL new_line

    ; use AX for A, BX for B
    MOV AH, 0
    MOV AL, A

    MOV BH, 0
    MOV BL, B

    cmmdc_loop:
        CMP AX, BX
        JE print_result; A == B -> print

        JA ax_bigger; A > B -> A = A - B

        SUB BX, AX; B > A -> B = B - A

        JMP cmmdc_loop
    
    ax_bigger:
        SUB AX, BX
        JMP cmmdc_loop
    
    print_result:
        PUSH AX; save AX in stack

        MOV AH, 9 ; print result message
        MOV DX, OFFSET n3
        INT 21h

        POP AX ; get AX from stack
        CALL print_num

main endp
end main