package project;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class myinterpreter{
	static Scanner Scan;
	
	//Table for all declared variables in the main program
	static HashMap<String, Integer> Vars;
	
	//String for the inputfile
	static String InputFile;
	
	//used to read input data from oustide file
	static BufferedReader Reader;
	
	//used to track indentation for the print function
	static int IndentCount;
	
	//table for tracking function objects
	static HashMap<String, Function> Functions;
	
	//Used to store the parse tree for a declared function and can be executed anytime it is invoked
	static class Function{
		//declared parameters
		ArrayList <String> parameters;
		
		//parameters that are passed in during run time
		ArrayList <String> passedParams;
		
		//value to be returned
		String returnParam;
		
		//used to track which value will recieve the return value
		boolean isReturn;
		
		//the internal parameter mapped to a value in their scope
		HashMap <String, Integer> finalParams;
		
		//parsed statement sequence, will be indepently executed at run time
		StmtSeq stmtSeq;
		
		//Constructor function
		Function(StmtSeq ss){
			isReturn = true;
			this.stmtSeq = ss;
			this.parameters = new ArrayList<String>();
			this.passedParams = new ArrayList<String>();
			this.finalParams = new HashMap<>();
		}
		
		
		//Independent execution branch for local function execution
		int StmtExec(Function func) {
			stmtSeq.ExecFunc(func);
			return finalParams.get(this.returnParam);
		}
		
	}
	
	static class Prog {
		static DeclSeq decSeq;
		static StmtSeq stmtSeq;
		
		static void Parse() {
			if(Scan.currentToken() == Core.PROGRAM) {
				Scan.nextToken();
			}else {
				System.out.println("Error: PROGRAM token expected.");
				System.exit(0);
			}
			decSeq = new DeclSeq();
			decSeq.Parse();
			if(Scan.currentToken() == Core.BEGIN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: BEGIN token expected.");
				System.exit(0);
			}
			stmtSeq = new StmtSeq();
			stmtSeq.Parse();
			if(Scan.currentToken() == Core.END) {
				Scan.nextToken();
			}else {
				System.out.println("Error: END token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() != Core.EOS) {
				System.out.println("Error: EOS token expected.");
				System.exit(0);
			}
		}
		
		
		static void Exec() {
			if(decSeq != null) {
				decSeq.Exec();
			}
			if (stmtSeq != null){
				stmtSeq.Exec();
			}
		}
		
		//prints for troubleshooting
		static void Print() {
			System.out.println("program ");
			IndentCount ++;
			decSeq.Print();
			IndentCount --;
			System.out.println("begin ");
			IndentCount ++;
			stmtSeq.Print();
			IndentCount --;
			System.out.println("end");
		}
	}
	
	
	static class DeclSeq{
		static Decl dec;
		static DeclSeq decSeq;
	
		
		void Parse() {
			
			if(Scan.currentToken() != Core.BEGIN) {
				dec = new Decl();
				dec.Parse();
			}
			
			if(Scan.currentToken() != Core.BEGIN) {
				decSeq = new DeclSeq();
				decSeq.Parse();
			}
			
		}
		
		
		void Exec() {
			if(dec != null) {
				dec.Exec();
			}
		}
		
		
		void Print() {
			if(dec != null) {
				dec.Print();
			}
		}
	}
	
	
	static class StmtSeq{
		Stmt stmt;
		StmtSeq stmtSeq;
		
		
		void Parse() {
			if(Scan.currentToken() != Core.END) {
				stmt = new Stmt();
				stmt.Parse();
			}
			if(Scan.currentToken() != Core.END && Scan.currentToken() != Core.ELSE && Scan.currentToken() != Core.ENDIF && Scan.currentToken() != Core.ENDWHILE && Scan.currentToken() != Core.ENDFUNC) {
				stmtSeq = new StmtSeq();
				stmtSeq.Parse();
			}
		}
	
		
		void Exec() {
			if(stmt != null) {
				stmt.Exec();
			}
			if (stmtSeq != null) {
				stmtSeq.Exec();
			}
		}
		
		void ExecFunc(Function func) {
			if(stmt != null) {
				stmt.ExecFunc(func);
			}
			if (stmtSeq != null) {
				stmtSeq.ExecFunc(func);
			}
		}
		
		
		void Print() {
			if(stmt != null) {
				stmt.Print();
			}
			if(stmtSeq != null) {
				stmtSeq.Print();
			}
		}
	}
	
	
	static class Decl{
		DeclId decId;
		DeclFunc decFunc;
		
		
		void Parse() {
			if(Scan.currentToken() == Core.INT) {
				Scan.nextToken();
				decId = new DeclId();
				decId.Parse();
				if(Scan.currentToken() == Core.SEMICOLON) {
					Scan.nextToken();
				}else {
					System.out.println("Error: SEMICOLON token expected.");
					System.exit(0);
				}
			} else {
				System.out.println("Error: INT token expected. No variables declared.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.ID){
				decFunc = new DeclFunc();
				decFunc.Parse();
			}
		}
		
		
		void Exec() {
			if(decId != null) {
				decId.Exec();
			}
			
			if(decFunc != null) {
				decFunc.Exec();
			}
		}
		
		void Print() {
			if(decId != null) {
				for(int i = 0; i < IndentCount; i ++) {
					System.out.print("  ");
				}
				System.out.print("int ");
				decId.Print();
				System.out.println(";");
			}
			
			if(decFunc != null) {
				decFunc.Print();
			}
		}
	}
	
	
	static class DeclId{
		IdList idList;
	
		void Parse() {
			idList = new IdList();
			idList.Parse();
		}	
		
		void Exec() {
			if(idList != null) {
				idList.Exec();
			}
		}
		
		void Print() {
			if(idList != null) {
				idList.Print();
			}
		}
	}
	
	
	static class DeclFunc{
		Id funcId;
		IdList params;
		StmtSeq stmtSeq;
		
		
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				funcId = new Id();
				funcId.Parse();
			}else {
				System.out.println("ID token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.LPAREN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: expected LPAREN token");
				System.exit(0);
			}
			
			params = new IdList();
			params.Parse();
			
			if(Scan.currentToken() == Core.RPAREN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: expected RPAREN token");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.BEGIN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: expected BEGIN token");
				System.exit(0);
			}
			stmtSeq = new StmtSeq();
			stmtSeq.Parse();
			if(Scan.currentToken() == Core.ENDFUNC) {
				Scan.nextToken();
			}else {
				System.out.println("Error: expected ENDFUNC");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Error: expecting token SEMICOLON");
				System.exit(0);
			}
		}
		
		
		void Exec() {
			Function func = new Function(stmtSeq);
			Functions.put(funcId.idName, func);
			
			if(params != null) {
				params.ExecFunc(func);
			}
		}
		
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print(funcId.idName);
			System.out.print("(");
			if(params != null) {
				params.Print();
			}
			System.out.println(") begin");
			IndentCount ++;
			if(stmtSeq != null) {
				stmtSeq.Print();
			}
			IndentCount --;
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.println("endfunc;");
			
		}
	}
	
	
	static class IdList{
		Id id;
		IdList idList;
		
	
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				id = new Id();
				id.Parse();
			}else {
				System.out.println("Error: ID token expected.");
				System.exit(0);
			}
			
			if(Scan.currentToken() == Core.COMMA) {
				Scan.nextToken();
				idList = new IdList();
				idList.Parse();
			}
			
		}
		
		
		
		void Exec() {
			if(id != null) {
				id.Exec();
			}
			if(idList != null) {
				idList.Exec();
			}
		}
		
		//Executed when the function is declared
		void ExecFunc(Function func) {
			
			if(!func.parameters.contains(id.idName)) {
				func.parameters.add(id.idName);
			}else {
				System.out.println("Error: function parameter declared more than once");
			}
			
			if(idList != null) {
				idList.ExecFunc(func);
			}
			
		}
		
		//Executed when the function is called
		void ExecParam(Function func) {
			//used to match the function parameters with the parameters being passed in
			String par = func.parameters.remove(0);
			func.parameters.add(par);
			func.finalParams.put(par, Vars.get(id.idName));
			//verifies the parameter to hold the return value
			if(func.isReturn) {
				func.returnParam = par;
				func.isReturn = false;
			}
			
			if(idList != null) {
				idList.ExecParam(func);
			}
		}
		
		//used to take input for an idlist
		void ExecInput() throws IOException {
			boolean negative = false;
			if(id != null) {
				int c = 0;	
				
				c = Reader.read();
				while ((c >57 || c < 48) && c != 45) {
					c = Reader.read();
				}
				
				if (c == 45) {
					negative = true;
					c = Reader.read();
				}
				
				int inputValue = 0;
				while(c != 32 && c != 13 && c != 11 && c != -1 && c != 9) {
					inputValue = inputValue * 10 + Character.getNumericValue(c);
					c = Reader.read();
				}
				
				if(negative) {
					inputValue *= -1;
					negative = false;
				}
				if(Vars.containsKey(id.idName)) {
					Vars.replace(id.idName, inputValue);
				}else {
					System.out.println("Error: " + id.idName + " has not been declared.");
					System.exit(0);
				}
			}
			if(idList != null) {
				idList.ExecInput();
			}
		}
		
		//takes input for an idlist when beign executed by a function
		void ExecInputFunc(Function func) throws IOException {
			boolean negative = false;
			if(id != null) {
				int c = 0;	
				
				c = Reader.read();
				while ((c >57 || c < 48) && c != 45) {
					c = Reader.read();
				}
				
				if (c == 45) {
					negative = true;
					c = Reader.read();
				}
				
				int inputValue = 0;
				while(c != 32 && c != 13 && c != 11 && c != -1 && c != 9) {
					inputValue = inputValue * 10 + Character.getNumericValue(c);
					c = Reader.read();
				}
				
				if(negative) {
					inputValue *= -1;
					negative = false;
				}
				
				if(func.finalParams.containsKey(id.idName)) {
					func.finalParams.replace(id.idName, inputValue);
				}else {
					System.out.println("Error: " + id.idName + " has not been delcared.");
				}
			}
			if(idList != null) {
				idList.ExecInputFunc(func);
			}
		}
		
		
		
		void Print() {
			if(id != null) {
				System.out.print(id.idName);
			}
			if(idList != null) {
				System.out.print(",");
				idList.Print();
			}
		}
		
		
	}
	
	
	static class Stmt{
		Assign a;
		IfThenElse ite;
		Loop l;
		In i;
		Out o;
		Func f;
		
		
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				a = new Assign();
				a.Parse();
			}else if(Scan.currentToken() == Core.IF) {
				Scan.nextToken();
				ite = new IfThenElse();
				ite.Parse();
			}else if(Scan.currentToken() == Core.WHILE) {
				Scan.nextToken();
				l = new Loop();
				l.Parse();
			}else if(Scan.currentToken() == Core.INPUT) {
				Scan.nextToken();
				i = new In();
				i.Parse();
			}else if(Scan.currentToken() == Core.OUTPUT) {
				Scan.nextToken();
				o = new Out();
				o.Parse();
			}else if(Scan.currentToken() == Core.BEGIN) {
				Scan.nextToken();
				f = new Func();
				f.Parse();
			} else if(Scan.currentToken() == Core.EOS) {
				System.out.println("Missing END token");
				System.exit(0);
			}
			else {
				System.out.println("Expected Statement token.");
				System.exit(0);
			}
		}
	
		
		void Exec() {
			if(a != null) {
				a.Exec();
			}else if(ite != null) {
				ite.Exec();
			}else if(l != null) {
				l.Exec();
			}else if(i != null) {
				i.Exec();
			}else if(o != null) {
				o.Exec();
			}else if(f != null) {
				f.Exec();
			}
		}
		
		void ExecFunc(Function func) {
			if(a != null) {
				a.ExecFunc(func);
			}else if(ite != null) {
				ite.ExecFunc(func);
			}else if(l != null) {
				l.ExecFunc(func);
			}else if(i != null) {
				i.ExecFunc(func);
			}else if(o != null) {
				o.ExecFunc(func);
			}else if(f != null) {
				f.Exec();
			}
		}
		
		void Print() {
			if(a != null) {
				a.Print();
			}
			if(ite != null) {
				ite.Print();
			}
			if(l != null) {
				l.Print();
			}
			if (i != null) {
				i.Print();
			}
			if (o != null) {
				o.Print();
			}
			if(f != null) {
				f.Print();
			}
		}
	}
	
	
	static class Func{
		Id id;
		IdList idList;
		Expr exp;
		int result;
		
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				id = new Id();
				id.Parse();
			}else {
				System.out.println("Error: ID token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.LPAREN) {
				Scan.nextToken();
			}else {
				System.out.println("Error LPAREN token expected.");
				System.exit(0);
			}
			idList = new IdList();
			idList.Parse();
			if(Scan.currentToken() == Core.RPAREN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: RPAREN token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Error: SEMICOLON token expected.");
				System.exit(0);
			}
		}
		
		void Exec() {
			Function func = Functions.get(id.idName);
			
			
			if(idList != null) {
				idList.ExecParam(func);
			}
			
			result = func.StmtExec(func);
			
			Vars.replace(idList.id.idName, result);
			
		}
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print("begin " + id.idName + "(");
			if(idList != null) {
				idList.Print();
			}
			System.out.println(");");
			
		}
	}
	
	
	static class Assign{
		String id;
		Expr exp;
	
	
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				id = Scan.getID();
				Scan.nextToken();
			}else {
				System.out.println("Error: ASSIGN token expected.");
				System.exit(0);
			}
			
			if(Scan.currentToken() == Core.ASSIGN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: ASSIGN token expected");
				System.exit(0);
			}
			
			exp = new Expr();
			exp.Parse();
			
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Error: SEMICOLON token expected");
				System.exit(0);
			}
		}
		
		
		void Exec() {
			if(exp != null) {
				exp.Exec();//returnign with -2
				Vars.replace(id, exp.expValue);
			}
			
		}
		
		//used when assigning values to the function variable. 
		//Uses a different table that is exclusive to the Function Object
		void ExecFunc(Function func) {
			if(exp != null) {
				exp.ExecFunc(func);
				func.finalParams.replace(id, exp.expValue);
			}
		}
		
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print(id+":=");
			exp.Print();
			System.out.println(";");
			
		}
	}
		
	
	static class In{
		IdList idList;
		
		void Parse() {
			if(Scan.currentToken() == Core.ID) {
				idList = new IdList();
				idList.Parse();
			}else {
				System.out.println("Error: ID token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Error: SEMICOLON token expected.");
				System.exit(0);
			}
		}
		void Exec() {
			if(idList != null) {
				try {
					idList.ExecInput();
				} catch (IOException e) {
					System.out.println("Error: input data not valid.");
					e.printStackTrace();
				}
				
			}
		}
		
		//used to store the idlist in the Function table
		void ExecFunc(Function func) {
			if(idList != null) {
				try {
					idList.ExecInputFunc(func);
				} catch (IOException e) {
					System.out.println("Error: input data not valid.");
					e.printStackTrace();
				}
				
			}
		}
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print("input ");
			if(idList != null) {
				idList.Print();
			}
			System.out.println(";");
		}
	}
	
	
	static class Out{
		Expr exp;
	
		void Parse() {
			exp = new Expr();
			exp.Parse();
			
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Expected Semicolon");
				System.exit(0);
			}
		}
		
		
		void Exec() {
			if(exp != null) {
				exp.Exec();
				System.out.println(exp.expValue);
			}
		}
		
		//Executes expressions that are assiciated with the Function object
		void ExecFunc(Function func) {
			if(exp!= null) {
				exp.ExecFunc(func);
				System.out.println(exp.expValue);
			}
		}
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print("output ");
			exp.Print();
			System.out.println(";");
		}
	}
	
	
	static class IfThenElse{
		Cond cond;
		StmtSeq thenStmt;
		StmtSeq elseStmt;
		boolean condResult;
		
		void Parse() {
			cond = new Cond();
			cond.Parse();
			if(Scan.currentToken() == Core.THEN) {
				Scan.nextToken();
			}else {
				System.out.println("Error: THEN token expected.");
				System.exit(0);
			}
			thenStmt = new StmtSeq();
			thenStmt.Parse();
			if(Scan.currentToken() == Core.ELSE) {
				Scan.nextToken();
				elseStmt = new StmtSeq();
				elseStmt.Parse();
			}
			if(Scan.currentToken() == Core.ENDIF) {
				Scan.nextToken();
			}else {
				System.out.println("Error: ENDIF token expected.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Error: SEMICOLON token expected.");
				System.exit(0);
			}
		}
		
		
		void Exec() {
			if(cond != null) {
				cond.Exec();
				condResult = cond.condValue;
			}
			if(thenStmt != null && condResult) {
				thenStmt.Exec();
			}
			if(elseStmt != null && !condResult) {
				elseStmt.Exec();
			}
		}	
		
		//Executes if then statements in association with the Function object
		void ExecFunc(Function func) {
			if(cond != null) {
				cond.ExecFunc(func);
				condResult = cond.condValue;
			}
			if(thenStmt != null && condResult) {
				thenStmt.ExecFunc(func);
			}
			if(elseStmt != null && !condResult) {
				elseStmt.ExecFunc(func);
			}
		}
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print("if ");
			if(cond != null) {
				cond.Print();
			}
			System.out.println(" then");
			if(thenStmt != null) {
				IndentCount++;
				thenStmt.Print();
				IndentCount--;
			}
			if(elseStmt != null) {
				for(int i = 0; i < IndentCount; i ++) {
					System.out.print("  ");
				}
				System.out.println("else");
				IndentCount++;
				elseStmt.Print();
				IndentCount--;
				for(int i = 0; i < IndentCount; i ++) {
					System.out.print("  ");
				}
				System.out.println("endif;");
			}
		}
		
		
	}
	
	
	static class Loop{
		Cond cond;
		StmtSeq stmtSeq;
		
		void Parse() {
			cond = new Cond();
			cond.Parse();
			if(Scan.currentToken() == Core.BEGIN) {
				Scan.nextToken();
			}else {
				System.out.println("Expected BEGIN token not found.");
				System.exit(0);
			}
			
			stmtSeq = new StmtSeq();
			stmtSeq.Parse();
			
			if(Scan.currentToken() == Core.ENDWHILE) {
				Scan.nextToken();
			}else {
				System.out.println("Expected ENDWHILE token not found.");
				System.exit(0);
			}
			if(Scan.currentToken() == Core.SEMICOLON) {
				Scan.nextToken();
			}else {
				System.out.println("Expected SEMICOLON token.");
				System.exit(0);
			}
			
		}
		void Exec() {
			if(cond != null) {
				cond.Exec();
			}
			while(cond.condValue) {
				stmtSeq.Exec();
				cond.Exec();
			}
		}
		
		//Executes loops associated with the Function Object
		void ExecFunc(Function func) {
			if(cond != null) {
				cond.ExecFunc(func);
			}
			while(cond.condValue) {
				stmtSeq.ExecFunc(func);
				cond.ExecFunc(func);
			}
		}
		
		void Print() {
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.print("while ");
			if(cond != null) {
				cond.Print();
			}
			System.out.println(" begin");
			IndentCount ++;
			if(stmtSeq != null) {
				stmtSeq.Print();
			}
			IndentCount --;
			for(int i = 0; i < IndentCount; i ++) {
				System.out.print("  ");
			}
			System.out.println("endwhile;");
		}
	}

	
	static class Cond{
		Cmpr compare;
		Cond cond;
		Cond orCond;
		boolean condValue;
		boolean negate;
		boolean orVariable;
		void Parse() {
			if(Scan.currentToken() == Core.NEGATION) {
				negate = true;
				Scan.nextToken();
				if(Scan.currentToken() == Core.LPAREN) {
					Scan.nextToken();
				}else {
					System.out.println("Error: LPAREN token expected.");
					System.exit(0);
				}
				cond = new Cond();
				cond.Parse();
				if(Scan.currentToken() == Core.RPAREN) {
					Scan.nextToken();
				}else {
					System.out.println("Error: RPAREN token expected.");
					System.exit(0);
				}
			}else {
				compare = new Cmpr();
				compare.Parse();
			}
			
			if(Scan.currentToken() == Core.OR) {
				Scan.nextToken();
				orVariable = true;
				orCond = new Cond();
				orCond.Parse();
			}
		}	
		
		void Exec() {
			if(compare != null) {
				compare.Exec();
			}
			if(cond != null) {
				cond.Exec();
			}
			if(orCond != null) {
				orCond.Exec();
			}
			if(negate) {
				condValue = !cond.condValue;
			}else {
				condValue = compare.result;
			}
			if(orVariable) {
				condValue = orCond.compare.result|| condValue;
			}
		}
		
		void ExecFunc(Function func) {
			if(compare != null) {
				compare.ExecFunc(func);
			}
			if(cond != null) {
				cond.ExecFunc(func);
			}
			if(negate) {
				condValue = !cond.condValue;
			}else {
				condValue = compare.result;
			}
			if(orVariable) {
				condValue = compare.result || cond.condValue;
			}
		}
		
		void Print() {
			
			if(negate) {
				System.out.print("!(");
				cond.Print();
				System.out.print(")");
			}else if(compare != null) {
				compare.Print();
			}else if(cond != null) {
				cond.Print();
			}
			
			if(orVariable) {
				System.out.print(" or ");
				orCond.Print();
			}
		}
	}
	
	
	static class Cmpr{
		Expr exp1;
		Expr exp2;
		boolean result;
		boolean equal;
		boolean less;
		boolean lessEqual;
		
		void Parse() {
			exp1 = new Expr();
			exp1.Parse();
			
			if(Scan.currentToken() == Core.EQUAL) {
				Scan.nextToken();
				if(Scan.currentToken() != Core.ID && Scan.currentToken()!= Core.CONST) {
					System.out.println("Error: "+ Scan.currentToken()+ " token is not valid in this context.");
					System.exit(0);
				}
				equal = true;
				exp2 = new Expr();
				exp2.Parse();
			}else if(Scan.currentToken() == Core.LESS) {
				Scan.nextToken();
				if(Scan.currentToken() != Core.ID && Scan.currentToken()!= Core.CONST) {
					System.out.println("Error: "+ Scan.currentToken()+ " token is not valid in this context.");
					System.exit(0);
				}
				less = true;
				exp2 = new Expr();
				exp2.Parse();
			}else if(Scan.currentToken() == Core.LESSEQUAL) {
				Scan.nextToken();
				if(Scan.currentToken() != Core.ID && Scan.currentToken()!= Core.CONST) {
					System.out.println("Error: "+ Scan.currentToken()+ " token is not valid in this context.");
					System.exit(0);
				}
				lessEqual = true;
				exp2 = new Expr();
				exp2.Parse();
			}
			
		}
		void Exec() {
			if(exp1 != null) {
				exp1.Exec();
			}
			if(exp2 != null) {
				exp2.Exec();
			}
			if(equal) {
				result = exp1.expValue == exp2.expValue;
			}else if(less) {
				result = exp1.expValue < exp2.expValue;
			}else if(lessEqual) {
				result = exp1.expValue <= exp2.expValue;
			}
		}
		
		void ExecFunc(Function func) {
			if(exp1 != null) {
				exp1.ExecFunc(func);
			}
			if(exp2 != null) {
				exp2.ExecFunc(func);
			}
			if(equal) {
				result = exp1.expValue == exp2.expValue;
			}else if(less) {
				result = exp1.expValue < exp2.expValue;
			}else if(lessEqual) {
				result = exp1.expValue <= exp2.expValue;
			}
		}
		
		void Print() {
			if(exp1 != null) {
				exp1.Print();
				if(equal) {
					System.out.print("=");
				}else if(less) {
					System.out.print("<");
				}else if(lessEqual) {
					System.out.print("<=");
				}
			}
			if(exp2 != null) {
				exp2.Print();
			}
			
		}
	}
	
	static class Expr{
		Term term;
		Expr exp;
		boolean add;
		boolean sub;
		int expValue;
		
		
		void Parse() {
			if(Scan.currentToken() != Core.SEMICOLON) {
				term = new Term();
				term.Parse();
				
				if(Scan.currentToken() == Core.ADD) {
					Scan.nextToken();
					add = true;
					exp = new Expr();
					exp.Parse();
				}else if(Scan.currentToken() == Core.SUB) {
					Scan.nextToken();
					sub = true;
					exp = new Expr();
					exp.Parse();
				}
			}
		}
		
		
		void Exec() {
			if(term != null) {
				term.Exec();
				expValue = term.termValue;
			}
			if(exp != null) {
				exp.Exec();
				if(add) {
					expValue += exp.expValue;
				}else if(sub) {
					expValue -= exp.expValue;
				}
			}
		}
		
		//Executes with Function Object
		void ExecFunc(Function func) {
			if(term != null) {
				term.ExecFunc(func);
				expValue = term.termValue;
			}
			if(exp != null) {
				exp.ExecFunc(func);
				if(add) {
					expValue += exp.expValue;
				}else if(sub) {
					expValue -= exp.expValue;
				}
			}
		}
		
		void Print() {
			if(term != null) {
				term.Print();
			}
			if(exp != null) {
				if(add) {System.out.print("+");}
				if(sub) {System.out.print("-");}
				exp.Print();
			}
		}
	}
	
	
	static class Term{
		Term term;
		Factor factor;
		int termValue;
		
		
		void Parse() {
			factor = new Factor();
			factor.Parse();
			if(Scan.currentToken() == Core.MULT) {
				Scan.nextToken();		
				term = new Term();
				term.Parse();
			}
		}
		void Exec() {
			if(factor != null) {
				factor.Exec();
				termValue = factor.value;
			}
			if(term != null) {
				term.Exec();
				termValue *= term.termValue;
			}
			
		}
		
		//Executes with Function Object
		void ExecFunc(Function func) {
			if(factor != null) {
				factor.ExecFunc(func);
				termValue = factor.value;
			}
			if(term != null) {
				term.ExecFunc(func);
				termValue *= term.termValue;
			}
		}
		
		void Print() {
			if(factor != null) {
				factor.Print();
			}
			if(term != null) {
				System.out.print("*");
				term.Print();
			}
		}
	}
	
	
	static class Factor{
		Id id;
		Expr exp;
		int value;
		boolean isConst;
		
		void Parse() {
			if(Scan.currentToken() == Core.CONST) {
				isConst = true;
				value = Scan.getCONST();
				Scan.nextToken();
			}else if(Scan.currentToken() == Core.ID) {
				//var is already in table from declaration
				id = new Id();
				id.Parse();
			}else if(Scan.currentToken() == Core.LPAREN) {
				Scan.nextToken();
				exp = new Expr();
				exp.Parse();
				if(Scan.currentToken() == Core.RPAREN) {
					Scan.nextToken();
				}else {
					System.out.println("Error: RPAREN token expected.");
					System.exit(0);
				}
			}
		}
		
		
		void Exec() {
			if(id != null) {
				if(Vars.containsKey(id.idName)) {
					value = Vars.get(id.idName);
				}else {
					System.out.println("Error: " + id.idName + " has not been declared");
				}
			}
			if(exp!= null) {
				exp.Exec();
				value = exp.expValue;
			}
		}
		
		//Executes with Function Object
		void ExecFunc(Function func) {
			if(id != null) {
				if(func.finalParams.containsKey(id.idName)) {
					value = func.finalParams.get(id.idName);
				}else {
					System.out.println("Error: " + id.idName + " has not been declared");
				}
			}
			if(exp!= null) {
				exp.ExecFunc(func);
				value = exp.expValue;
			}
		}
		
		void Print() {
			if(id != null) {
				System.out.print(id.idName);
			}
			if(isConst) {System.out.print(value);}
			if(exp != null) {
				System.out.print("(");
				exp.Print();
				System.out.print(")");
			}
		}
	}
	
	
	static class Id{
		String idName;

		void Parse() {
			idName = Scan.getID();
			Scan.nextToken();
		}
		
		void Exec() {
			if(idName != null) {
				if(!Vars.containsKey(idName)) {
					Vars.put(idName,  null);
				}else {
					System.out.println("Error: " + idName + " has already been declared");
				}
			}		
		}
	}


	public static void main(String[] args) {
		Scan = new Scanner(args[0]);
		Vars = new HashMap<>();
		IndentCount = 0;
		InputFile = args[1];
		Functions = new HashMap<>();
		
		try {
			Reader = new BufferedReader(new FileReader(InputFile));
		} catch (FileNotFoundException e) {
			System.out.println("Error: Input file not found.");
			e.printStackTrace();
		}
		
		Prog.Parse();
		Prog.Print();
		Prog.Exec();
	}
		
}

