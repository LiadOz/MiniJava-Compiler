/***************************/
/* Based on a template by Oren Ish-Shalom */
/***************************/

/*************/
/* USER CODE */
/*************/
import java_cup.runtime.*;



/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/

/*****************************************************/
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */
/*****************************************************************************/
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine()    { return yyline + 1; }
	public int getCharPos() { return yycolumn;   }
%}

/***********************/
/* MACRO DECALARATIONS */
/***********************/

line_terminator	= \r|\n|\r\n
whitespace		= [\t ] | {line_terminator}
integer		    = 0 | [1-9][0-9]*
ID				= [a-zA-Z][a-zA-Z0-9_]*
block_comment = "/*" ~"*/"
line_comment = "//"~\n


/******************************/
/* DOLAR DOLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/









/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

{block_comment}			{}
{line_comment}			{}

// Literals
{integer}               {return symbol(sym.INTEGER_LITERAL, Integer.parseInt(yytext()));}
true 					{return symbol(sym.TRUE);}
false 					{return symbol(sym.FALSE);}

//Meta language
\{						{return symbol(sym.LBRACE);}
\}						{return symbol(sym.RBRACE);}
\[						{return symbol(sym.LBRACKET);}
\]						{return symbol(sym.RBRACKET);}
\(						{return symbol(sym.LPAREN);}
\)						{return symbol(sym.RPAREN);}
,						{return symbol(sym.COMMA);}
;						{return symbol(sym.SEMICOLON);}
\!						{return symbol(sym.EXCLAMATION);}
\.						{return symbol(sym.DOT);}
"="						{return symbol(sym.EQUALS);}

// Binary Operators

\<						{return symbol(sym.LESSTHAN);}
&&						{return symbol(sym.AND);}
\+						{return symbol(sym.PLUS);}
\*						{return symbol(sym.MULT);}
\-                      {return symbol(sym.MINUS);}


// Class Related
String					{return symbol(sym.STRING);}
public					{return symbol(sym.PUBLIC);}
class					{return symbol(sym.CLASS);}
static					{return symbol(sym.STATIC);}
void					{return symbol(sym.VOID);}
main					{return symbol(sym.MAIN);}
extends 				{return symbol(sym.EXTENDS);}
new 					{return symbol(sym.NEW);}
this 					{return symbol(sym.THIS);}

// Control Flow
if 						{return symbol(sym.IF);}
else					{return symbol(sym.ELSE);}
while					{return symbol(sym.WHILE);}

// Types and methods

return 					{return symbol(sym.RETURN);}
boolean 				{return symbol(sym.BOOLEAN);}
int 					{return symbol(sym.INTEGER);}
System\.out\.println	{return symbol(sym.PRINT);}
length					{return symbol(sym.LENGTH);}

{ID}                    {return symbol(sym.IDENTIFIER, yytext());}
{whitespace}            {}
}