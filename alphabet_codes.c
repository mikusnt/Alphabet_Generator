#include "alphabet_codes.h"

const uint8_t uiAlphabet[ALPHABET_SIZE][5] PROGMEM = {
	// A first letter, y = 0
	{ 0x7C, 0x12, 0x11, 0x12, 0x7C },
	// B second letter, y = 1
	{ 0x7F, 0x49, 0x49, 0x49, 0x3E },
	// C third letter, y = 2
	{ 0x1C, 0x22, 0x41, 0x41, 0x00 }
};

const uint8_t uiAlLength[ALPHABET_SIZE] PROGMEM = {
	5, // A first letter, y = 0
	5, // B second letter, y = 1
	4 // C third letter, y = 2
};