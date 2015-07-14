import java.util.Stack;

/**
 * Created by Jason on 7/13/15.
 */
public class Chip8 {

    private int PC;
    private int[] regV = new int[16];
    private int[] regHP48 = new int[8]; // 8 Data registers (8 bit each), HP48
    private int regI; //1 Address register
    private byte[] memory = new byte[0x1000];

    Stack<Integer> stack;


    private int delay_timer;
    private int sound_timer;

    // Current Opcode
    private int opCode;
    private int pOpCode; // previous

    private boolean isRunning;


    public int getPC() { return PC; }

    public void setPC(int PC) { this.PC = PC & 0xFFFF; }

    public int getReg(int index) { return regV[index];   }

    public void setReg(int index, int value) { regV[index] = value & 0xFF; }

    public int getRegI() { return regI; }

    public void setRegI(int value) { regI = value & 0xFFFF;}

    public int getOpCode() { return opCode; }

    public int getPOpCode() { return pOpCode; }

    public int getDelay_timer() { return delay_timer;   }

    public int getSound_timer() { return sound_timer; }

    public boolean getIsRunning() { return isRunning; }


    public void init() {
        for(int i = 0; i < 0xF; i++) {
            regV[i] = 0;

            if(i < 8) {
                regHP48[i] = 0;
            }
        }

        opCode = 0;
        pOpCode = 0;

        //Program counter starts at 0x200
        PC = 0x200;

        regI = 0;
        sound_timer = 0;
        delay_timer = 0;
        stack = new Stack();


    }

    public void retrieveOpcode() {
        pOpCode = opCode;
        opCode = memory[PC] << 8 | (0x00FF & memory[PC+1]);
    }


    public void cpuTick(boolean timerDecrease) {

        int nibble1 = opCode & 0xF000 >> 12;
        int nibble2 = opCode & 0x0F00 >> 8;
        int nibble3 = opCode & 0x00F0 >> 4;
        int nibble4 = opCode & 0x000F >> 0;


        switch(nibble1) {
            case(0x0):
                if(nibble2 == 0x0 && nibble3 == 0x0E) {
                    if(nibble4 == 0x0) {
                        //cls
                    }
                    else if(nibble4 == 0xE) {
                        //rts
                    }
                }
                else {
                    //0NNN
                }
                break;
            case(0x1):
                //1NNN Jump to address NNN
                break;

            case(0x2):
                //jsr nnn
                //jump to subroutine at address NNN
                //16 levels maximum
                break;

            case(0x3):
                //3XRR
                //skeq vx, rr
                //skip next istruction if register VX == constant RR
                break;

            case(0x4):
                //4XRR
                //skne vx,rr
                //skip next intruction if register VX != constant RR
                break;
            case(0x5):
                //5XY0
                //skeq vx,vy
                //skip next instruction if register VX == register VY
                break;
            case(0x6):
                //6XRR
                //mov vx,rr
                //move constant RR to register VX
                break;
            case(0x7):
                //7XRR
                //add vx,rr
                //add constant RR to register VX
                //Note: No carry generated
                break;
            case(0x8):
                if(nibble4 == 0x0) {
                    //8XY0
                    //mov vx,vy
                    //move register VY into VX
                }
                else if(nibble4 == 0x1) {
                    //8XY1
                    //or vx,vy
                    //or register VY with register VX, store result into register VX

                }
                else if(nibble4 == 0x2) {
                    //8XY2
                    //and vx,vy
                    //and register VY with register VX, store result into register VX
                }
                else if(nibble4 == 0x3) {
                    //8XY3
                    //xor vx,vy
                    //exclusive or register VY with register VX, store result into register VX
                }
                else if(nibble4 == 0x4) {
                    //8XY4
                    //add vx,vy
                    //add register VY to VX, store result in register VX, carry stored in register VF
                }
                else if(nibble4 == 0x5) {
                    //8XY5
                    //sub vx,vy
                    //subtract register VY from VX, borrow stored in register VF
                    //Note: register VF set to 1 if borrows
                }
                else if(nibble4 == 0x6) {
                    //8X06
                    //shr vx
                    //shift register VX right, bit 0 goes into register VF

                }
                else if(nibble4 == 0x7) {
                    //8XY7
                    //rsb vx,vy
                    //subtract register VX from register VY, result stored in register VX
                }
                else if(nibble4 == 0xE) {
                    //8X0E
                    //shl vx
                    //shift register VX left, bit 7 stored into register VF
                }

                break;
            case(0x9):
                //9XY0
                //skne vx,vy
                //skip next instruction if register VX != register VY
                break;
            case(0xA):
                //ANNN
                //mvi nnn
                //Load index register (I) with constant NNN
                break;
            case(0xB):
                //BNNN
                //jmi nnn
                //Jump to address NNN + register V0
                break;
            case(0xC):
                //CXKK
                //rand vx,kk
                //register VX = random number AND KK
                break;
            case(0xD):
                //DXYN
                //sprite vx,vy,n
                //Draw sprite at screen location (register VX,register VY) height N
                //Note: Sprites stored in memory at location in index register (I), maximum 8bits wide. Wraps around the screen. If when drawn, clears a pixel, register VF is set to 1 otherwise it is zero. All drawing is XOR drawing (e.g. it toggles the screen pixels)
                break;
            case(0xE):
                if(nibble3 == 0x9 && nibble4 == 0xE) {
                    //ek9e
                    //skpr k
                    //skip if key (register rk) pressed
                    //Note: The key is a key number, see the chip-8 documentation
                }
                else if(nibble3 == 0xA && nibble4 == 0x1) {
                    //eka1
                    //skup k
                    //skip if key (register rk) not pressed
                }
                break;
            case(0xF):

                if(nibble3 == 0x0) {
                    if(nibble4 == 0x7) {
                        //FX07
                    }
                    else if(nibble4 == 0xA) {
                        //FX0A
                    }
                }
                else if(nibble3 == 0x1) {

                    if(nibble4 == 0x5) {
                        //FX15
                    }
                    else if(nibble4 == 0x8) {
                        //FX18
                    }
                    else if(nibble4 == 0xE) {
                        //FX1E
                    }
                }
                else if(nibble3 == 0x2) {
                    //FX29
                }
                else if(nibble3 == 0x3) {
                    //FX33
                }
                else if(nibble3 == 0x5) {
                    //FX55
                }
                else if(nibble3 == 0x6) {
                    //FX65
                }
                break;
        }//end switch
    }//end cpuTick

}//end Chip8