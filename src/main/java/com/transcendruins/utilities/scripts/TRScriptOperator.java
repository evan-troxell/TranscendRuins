package com.transcendruins.utilities.scripts;

import java.util.ArrayList;
import java.util.HashMap;

import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedEntry;

public abstract class TRScriptOperator {

    private static final HashMap<String, TRScriptOperator> OPERATORS = createOperators();

    public abstract Object evaluate(ArrayList<TRScriptValue> args);

    public abstract boolean invalidArgs(int args);

    public static final HashMap<String, TRScriptOperator> createOperators() {

        HashMap<String, TRScriptOperator> operators = new HashMap<>();

        operators.put("max", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                double max = Double.NEGATIVE_INFINITY;

                for (double value : TRScriptValue.evaluateDoubles(args)) {

                    if (value > max) {

                        max = value;
                    }
                }
                return max;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 2;
            }
        });

        operators.put("min", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                double min = Double.POSITIVE_INFINITY;

                for (double value : TRScriptValue.evaluateDoubles(args)) {

                    if (value < min) {

                        min = value;
                    }
                }
                return min;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 2;
            }
        });

        operators.put("&&", new TRScriptOperator() {

            @Override
            public final Boolean evaluate(ArrayList<TRScriptValue> args) {

                for (boolean value : TRScriptValue.evaluateBooleans(args)) {

                    if (!value) {

                        return value;
                    }
                }

                return true;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 2;
            }
        });

        operators.put("||", new TRScriptOperator() {

            @Override
            public final Boolean evaluate(ArrayList<TRScriptValue> args) {

                for (boolean value : TRScriptValue.evaluateBooleans(args)) {

                    if (value) {

                        return value;
                    }
                }
                
                return false;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 2;
            }
        });

        operators.put("+", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                double sum = 0;

                for (double value : TRScriptValue.evaluateDoubles(args)) {

                    sum += value;
                }
                
                return sum;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 1;
            }
        });

        operators.put("-", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                if (args.size() == 1) {

                    return -args.get(0).evaluateDouble();
                }
                
                return args.get(0).evaluateDouble() - args.get(1).evaluateDouble();
            }

            @Override
            public boolean invalidArgs(int args) {

                return args != 1 && args != 2;
            }
        });

        operators.put("*", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                double prod = 0;

                for (double value : TRScriptValue.evaluateDoubles(args)) {

                    prod *= value;
                }
                
                return prod;
            }

            @Override
            public boolean invalidArgs(int args) {

                return args < 2;
            }
        });

        operators.put("/", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                if (args.size() == 1) {

                    return 1.0 / args.get(0).evaluateDouble();
                }

                return args.get(0).evaluateDouble() / args.get(1).evaluateDouble();
            }

            @Override
            public boolean invalidArgs(int args) {

                return args != 1 && args != 2;
            }
        });
        
        operators.put("%", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                return args.get(0).evaluateDouble() % args.get(1).evaluateDouble();
            }

            @Override
            public boolean invalidArgs(int args) {

                return args != 2;
            }
        });

        operators.put("pow", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                return Math.pow(args.get(0).evaluateDouble(), args.get(1).evaluateDouble());
            }

            @Override
            public boolean invalidArgs(int args) {

                return args != 2;
            }
        });

        operators.put("log", new TRScriptOperator() {

            @Override
            public final Double evaluate(ArrayList<TRScriptValue> args) {

                double first = args.get(0).evaluateDouble();
                double second = args.get(1).evaluateDouble();

                if (first < 0 || second < 0) {

                    return null;
                }

                if (first == 0 && second != 0) {

                    return Double.NEGATIVE_INFINITY;
                }

                if (first != 0 && second == 0) {

                    return 0d;
                }

                if (first != 1 && second == 1) {

                    return (first < 1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
                }

                if (second == first && (first == 0 || first == 1)) {

                    return null;
                }

                return Math.log(args.get(0).evaluateDouble()) / Math.log(args.get(1).evaluateDouble());
            }

            @Override
            public boolean invalidArgs(int args) {

                return args != 2;
            }
        });
    
        return operators;
    }

    public static final TRScriptOperator getOperator(TracedEntry<String> operatorEntry) throws UnexpectedValueException {

        String operatorName = operatorEntry.getValue();

        TRScriptOperator operator = OPERATORS.get(operatorName);

        if (operator == null) {

            throw new UnexpectedValueException(operatorEntry);
        }

        return operator;
    }
}