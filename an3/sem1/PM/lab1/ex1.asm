dosseg

.model small

.stack 100h; 100h = 256 bytes of stack space

.data
    A DB 0
    B DB 0
    C DB 0
    n1 DB "Introduceti primul numar: $"
    n2 DB "Introduceti al doilea numar: $"
    n3 DB "Introduceti al treilea numar: $"
    result DB "Rezultatul este: $"

.code

new_line proc
    MOV AH, 2
    MOV DL, 10; ASCII code for new line
    INT 21h; DOS interrupt
    ret; return
new_line endp

main proc
    MOV AX, @data; initialize data segment
    MOV DS, AX; DS points to data segment
    
    ; read first number
    MOV AH, 9
    MOV DX, OFFSET n1; display message
    INT 21h; DOS interrupt
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
    SUB AL, 30h; convert ASCII to digit
    MOV B, AL
    CALL new_line

    ; read third number
    MOV AH, 9
    MOV DX, OFFSET n3; display message
    INT 21h; DOS interrupt
    MOV AH, 1
    INT 21h
    SUB AL, 30h; convert ASCII to digit
    MOV C, AL
    CALL new_line

    ; print result message
    MOV AH, 9
    MOV DX, OFFSET result
    INT 21h; DOS interrupt

    ; calculate result
    MOV AL, A; move A to AL
    ADD AL, B; add B to AL
    SUB AL, C; subtract C from AL

    ; print result
    ADD AL, 30h; convert digit to ASCII
    MOV AH, 2
    MOV DL, AL; move AL to DL
    INT 21h; DOS interrupt
    MOV AH, 4Ch; terminate program
    INT 21h; DOS interrupt

main endp

end main
