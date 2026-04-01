class invoke {
public Object invoke( Object proxy, Method method, Object[] args )
			throws Throwable
		{
			try {
				return invokeImpl( proxy, method, args );
			} catch ( TargetError te ) {
				// Unwrap target exception.  If the interface declares that
				// it throws the ex it will be delivered.  If not it will be
				// wrapped in an UndeclaredThrowable
				throw te.getTarget();
			} catch ( EvalError ee ) {
				// Ease debugging...
				// XThis.this refers to the enclosing class instance
				if ( Interpreter.DEBUG )
					Interpreter.debug( "EvalError in scripted interface: "
					+ XThis.this.toString() + ": "+ ee );
				throw ee;
			}
		}
}
