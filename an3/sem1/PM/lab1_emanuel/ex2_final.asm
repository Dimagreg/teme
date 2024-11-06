dosseg

; (2A - 3B)/5C -> catul si restul

; A = 6
; B = 1
; C = 1
; cat = 1
; rest = 4

.model small

.stack 1000

.data
    A DB 0
    B DB 0
    C DB 0
    n1 DB "Introduceti primul numar: $"
    n2 DB "Introduceti al doilea numar: $"
    n3 DB "Introduceti al treilea numar: $"

    result_cat DB "Catul este: $"
    result_rest DB "Restul este: $"
    rest db 0
    cat db 0

.code

new_line proc
    MOV AH, 2
    MOV DL, 10; ASCII code for new line
    INT 21h; DOS interrupt
    RET; RETurn
new_line endp

print_num proc
    MOV AH, 2
    MOV DL, AL
    ADD DL, '0'
    INT 21h
    RET
endp

main proc
    MOV AX, @data; initialize data segment
    MOV DS, AX; DS poINTs to data segment
    
    ; read first number
    MOV AH, 9
    MOV DX, OFFSET n1; display message
    INT 21h; DOS INTerrupt
    MOV AH, 1; read character from keyboard
    INT 21h; DOS interrupt
    SUB AL, 30h; convert ASCII to digit
    MOV A, AL; save character in A
    CALL new_line; new line
    
    ; read second number
    MOV AH, 9
    MOV DX, OFFSET n2; display message
    INT 21h; DOS interrupt
    MOV AH, 1
    INT 21h
    SUB AL, '0'; convert ASCII to digit
    MOV B, AL
    CALL new_line

    ; read third number
    MOV AH, 9
    MOV DX, OFFSET n3; display message
    INT 21h; DOS interrupt
    MOV AH, 1
    INT 21h
    SUB AL, '0'; convert ASCII to digit
    MOV C, AL
    CALL new_line

    ; (2A - 3B)/5C -> catul si restul

    ; A = 7
    ; B = 1
    ; C = 1
    ; cat = 2
    ; rest = 1

    MOV AL, A
    MOV BL, 2
    MUL BL
    MOV BL, AL ; BL = 2A

    MOV AL, B
    MOV CL, 3   
    MUL CL
    MOV CL, AL ; CL = 3B

    SUB BL, CL  ; BL = (2A - 3B)

    MOV AL, C
    MOV DL, 5
    MUL DL
    MOV DL, AL ; DL = 5C

    XOR AH, AH; clear AH
    MOV AL, BL
    DIV DL

    MOV cat, AL
    MOV rest, AH

    MOV AH, 9
    MOV DX, OFFSET result_cat; display message
    INT 21h; DOS interrupt

    MOV AL, cat
    CALL print_num
    CALL new_line

    MOV AH, 9
    MOV DX, OFFSET result_rest; display message
    INT 21h; DOS interrupt

    MOV AL, rest
    CALL print_num
    
    ; Exit program
    MOV AH, 004Ch
    INT 21h
main endp

end main
