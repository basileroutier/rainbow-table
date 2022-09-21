/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package g54018.rainbowtable;

public class SscanfFormat {
	protected int width;
	protected int precision;
	protected StringBuffer pre;
	protected char post;
	protected boolean leadingZeroes;
	protected boolean showPlus;
	protected boolean alternate;
	protected boolean showSpace;
	protected boolean leftAlign;
	protected boolean groupDigits;
	protected char fmt;	                // one of cdeEfgGiosxXos
	protected boolean countSignInLen;
	
	protected String format;
	protected String source;

	/**
	 * Formats a number in a printf format, like C.
	 *
	 * @param s      the format string following printf format string
	 *               The string has a prefix, a format code and a suffix. The prefix and suffix
	 *               become part of the formatted output. The format code directs the
	 *               formatting of the (single) parameter to be formatted. The code has the
	 *               following structure
	 *               <ul>
	 *               <li> a <b>%</b> (required)
	 *
	 *               <li> a modifier (optional)
	 *               <dl>
	 *               <dt> + <dd> forces display of + for positive numbers
	 *               <dt> ~ <dd> do not count leading + or - in length
	 *               <dt> 0 <dd> show leading zeroes
	 *               <dt> - <dd> align left in the field
	 *               <dt> space <dd> prepend a space in front of positive numbers
	 *               <dt> # <dd> use "alternate" format. Add 0 or 0x for octal or hexadecimal numbers.
	 *               Don't suppress trailing zeroes in general floating point format.
	 *               <dt> , <dd> groups decimal values by thousands (for 'diuxXb' formats)
	 *               </dl>
	 *
	 *               <li> an integer denoting field width (optional)
	 *
	 *               <li> a period (<b>.</b>) followed by an integer denoting precision (optional)
	 *
	 *               <li> a format descriptor (required)
	 *               <dl>
	 *               <dt>f <dd> floating point number in fixed format,
	 *               <dt>e, E <dd> floating point number in exponential notation (scientific format).
	 *               The E format results in an uppercase E for the exponent (1.14130E+003), the e
	 *               format in a lowercase e,
	 *               <dt>g, G <dd> floating point number in general format (fixed format for small
	 *               numbers, exponential format for large numbers). Trailing zeroes are suppressed.
	 *               The G format results in an uppercase E for the exponent (if any), the g format
	 *               in a lowercase e,.
	 *               <dt>d, i <dd> signed long and integer in decimal,
	 *               <dt>u <dd> unsigned long or integer in decimal,
	 *               <dt>x <dd> unsigned long or integer in hexadecimal,
	 *               <dt>o <dd> unsigned long or integer in octal,
	 *               <dt>b <dd> unsigned long or integer in binary,
	 *               <dt>s <dd> string,
	 *               <dt>c <dd> character,
	 *               <dt>l, L <dd> boolean in lower or upper case (for booleans and int/longs).
	 *               </dl>
	 *               </ul>
	 */
	public SscanfFormat(String source, String format) {
		this.source = source;
		this.format = format;
	}

	
	public boolean prepareNextParseParam() {
		if(format == null)
			return false;
		
		width = 0;
		precision = -1;
		pre = new StringBuffer();
		post = 0;
		leadingZeroes = false;
		showPlus = false;
		alternate = false;
		showSpace = false;
		leftAlign = false;
		countSignInLen = true;
		fmt = ' ';

		int i = 0;
		int length = format.length();
		int parseState;                 // 0 = prefix, 1 = flags, 2 = width, 3 = precision, 4 = format, 5 = end

		// 0: parse string prefix upto first '%'.
		while (true) {
			if (i >= length) {
				return false;
			}
			char c = format.charAt(i);
			if (c != '%') {
				pre.append(c);
				i++;
				continue;
			}
			if (i >= length - 1) {
				throw new IllegalArgumentException("Format string can not end with '%'.");
			}
			if (format.charAt(i + 1) == '%') {       // double '%%'
				pre.append('%');
				i += 2;
				continue;
			}
			parseState = 1;                 // single % founded
			i++;
			break;
		}

		// 1: parse flags
		flagsloop:
		while (parseState == 1) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = format.charAt(i);
			switch (c) {
				case ' ': showSpace = true; break;
				case '-': leftAlign = true; break;
				case '+': showPlus = true; break;
				case '0': leadingZeroes = true; break;
				case '#': alternate = true; break;
				case '~': countSignInLen = false; break;
				case ',': groupDigits = true; break;
				default:
					parseState = 2;
					break flagsloop;
			}
			i++;
		}

		// 2: parse width
		while (parseState == 2) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = format.charAt(i);
			if ((c >= '0') && (c <= '9')) {
				width = (width * 10) + format.charAt(i) - '0';
				i++;
				continue;
			}
			if (format.charAt(i) == '.') {
				parseState = 3;
				precision = 0;
				i++;
			} else {
				parseState = 4;
			}
			break;
		}

		// 3: parse precision
		while (parseState == 3) {
			if (i >= length) {
				parseState = 5;
				break;
			}
			char c = format.charAt(i);
			if ((c >= '0') && (c <= '9')) {
				precision = (precision * 10) + format.charAt(i) - '0';
				i++;
				continue;
			}
			parseState = 4;
			break;
		}

		// 4: parse format
		if (parseState == 4) {
			if (i < length) {
				fmt = format.charAt(i);
				i++;				
			}
		}

		if(i < length){
			post = format.charAt(i);
			format = format.substring(i);
		} else {
			format = null;
		}
		
		return true;
	}
	

	// ---------------------------------------------------------------- public form methods

	/**
	 * Formats a character into a string (like sprintf in C).
	 */
	public Character parse(char value) {
		if (fmt != 'c') {
			throw new IllegalArgumentException("Invalid character format: '" + fmt + "' is not 'c'.");
		}
		
		int index = parseToStartOfFormat();
		
		if(index != -1) {
			Character c = Character.valueOf(source.charAt(index));
			if(index < source.length()-1)
				source = source.substring(index+1);
			return c;
		}

		return null;
	}
	
	
	/**
	 * Formats a boolean into a string (like sprintf in C).
	 */
//	public String form(boolean value) {
//		
//		if (fmt == 'l') {
//			return pad(value ? "true" : "false");
//		}
//		else if (fmt == 'L') {
//			return pad(value ? "TRUE" : "FALSE");
//		}
//		throw new IllegalArgumentException("Invalid boolean format: '" + fmt + "' is not one of 'lL'.");
//	}

	/**
	 * Formats a double into a string (like sprintf in C).
	 */
//	public String form(double x) {
//		String r;
//
//		if (precision < 0) {
//			precision = 6;
//		}
//
//		int s = 1;
//		if (x < 0) {
//			x = -x;
//			s = -1;
//		}
//		if (fmt == 'f') {
//			r = fixedFormat(x);
//		} else if (fmt == 'e' || fmt == 'E' || fmt == 'g' || fmt == 'G') {
//			r = expFormat(x);
//		} else {
//			throw new IllegalArgumentException("Invalid floating format: '" + fmt + "' is not one of 'feEgG'.");
//		}
//		return pad(sign(s, r));
//	}

	/**
	 * Formats a long integer into a string (like sprintf in C).
	 */
	public Object parse(long x) {
		
		Object retval = null;
		int s = 0;
		int index;
		
		index = parseToStartOfFormat();
		if(index == -1)
			return null;
		
		switch (fmt) {
			case 'd':
			case 'i': {
				int v = 0;
				if(source.charAt(index) == '-') {
					s = -1;
					++index;
				}  else {
					s = 1;
				}
				int sLen = source.length();
				boolean foundNumber = false;
				while(index < sLen) {
					char c = source.charAt(index);
					
					if(c >= '0' && c <= '9') {
						v = (v * 10) + c - '0';
						++index;
						foundNumber = true;
						if(width > 0 && --width == 0)
							break;
					} else {
						break;
					}
				}
				
				if(foundNumber) {
					v *= s;
					retval = Integer.valueOf(v);
				} 
			} break;
			
			case 'u': {
				int v = 0;
				int sLen = source.length();
				boolean foundNumber = false;
				while(index < sLen) {
					char c = source.charAt(index);
					
					if(c >= '0' && c <= '9') {
						v = (v * 10) + c - '0';
						++index;
						foundNumber = true;
						if(width > 0 && --width == 0)
							break;
					} else {
						break;
					}
				}
				
				if(foundNumber) {
					retval = Integer.valueOf(v);
				} 
			} break;
	
			case 'x':
			case 'X': {
				int v = 0;
				int sLen = source.length();
				boolean foundNumber = false;
				while(index < sLen) {
					char c = source.charAt(index);
					
					if(c >= '0' && c <= '9') {
						v = (v * 16) + c - '0';
						++index;
						foundNumber = true;
						if(width > 0 && --width == 0)
							break;
					} else if(c >= 'a' && c <= 'f') {
						v = (v * 16) + c - 'a' + 10;	
						++index;
						foundNumber = true;
						if(width > 0 && --width == 0)
							break;
					} else if(c >= 'A' && c <= 'F') {
						v = (v * 16) + c - 'A' + 10;	
						++index;
						foundNumber = true;
						if(width > 0 && --width == 0)
							break;
					} else {
						break;
					}
				}
				
				if(foundNumber) {
					retval = Integer.valueOf(v);
				} 
			} break;
			
			default:
				throw new IllegalArgumentException("Invalid number format: '" + fmt + "' is not one of 'diuoxX'.");
		}
		
		if(retval != null) {
			
			if(index < source.length())
				source = source.substring(index);
		}
		
		return retval;
	}

	/**
	 * Formats an integer into a string (like sprintf in C).
	 */
	public Object parse(int x) {
		return parse((long)x);
	}

	/**
	 * Formats a string into a larger string (like sprintf in C).
	 */
	public Object parse(String s) {
		if (fmt != 's') {
			throw new IllegalArgumentException("Invalid long format: '" + fmt + "' is not 's'.");
		}
		
		int index = parseToStartOfFormat();
		int startIndex = index;
		
		if(index == -1) {
			return null;
		}
		int sLen = source.length();
		while(index < sLen) {
			char c = source.charAt(index++);
			
			if(precision > 0) {
				if(--precision == 0)
					break;
			} else if(width > 0) {
				if(--width == 0)
					break;
			} else if(c == post) {
				--index;
				break;
			} 
		}
		
		String retval = null;
		if(index != startIndex) {
			retval = source.substring(startIndex, index);
		}
		
		if(index < source.length())
			source = source.substring(index);
		

		return retval;
	}
	
	private int parseToStartOfFormat() {
		int i;
		int sLen = source.length();
		int fLen = pre.length();
		for(i = 0; i < sLen && i < fLen; ++i) {
			if(source.charAt(i) != pre.charAt(i)) {
				return -1;
			}
		}
		
		if(i < sLen && i == fLen) {
			return i;
		}
		
		return -1;
	}
	

}