package com.transcendruins.utilities.scripts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.transcendruins.utilities.exceptions.propertyexceptions.ArrayLengthException;
import com.transcendruins.utilities.exceptions.propertyexceptions.MissingPropertyException;
import com.transcendruins.utilities.exceptions.propertyexceptions.PropertyTypeException;
import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedArray;
import com.transcendruins.utilities.json.TracedDictionary;
import com.transcendruins.utilities.json.TracedEntry;

public final class TRScript {

    private final Value value;

    public TRScript(TracedEntry<?> valueEntry) {

        value = new Value(valueEntry);
    }

    public boolean evaluate() {

        return value.evaluateBoolean();
    }
    
    private static final class Value {

        private static final int NULL = -1;

        private static final int BOOLEAN = 1;
        private static final int LONG = 2;
        private static final int DOUBLE = 3;

        private static final int EXPRESSION = 4;
        private static final int EVALUATION = 5;

        private final int type;
        private final Object value;

        @SuppressWarnings("unchecked")
        public Value(TracedEntry<?> valueEntry) throws UnexpectedValueException, MissingPropertyException, PropertyTypeException {

            Object rawValue = valueEntry.getValue();
            
            type = switch (rawValue) {

                case Boolean boolVal -> {
                    
                    value = boolVal;
                    yield BOOLEAN;
                }

                case Long longVal -> {
                    
                    value = longVal;
                    yield LONG;
                }

                case Double doubVal -> {
                    
                    value = doubVal;
                    yield DOUBLE;
                }

                case TracedDictionary exprVal -> {
                    
                    value = new Expression((TracedEntry<TracedDictionary>) valueEntry);
                    yield EXPRESSION;
                }

                case String evalVal -> {

                    value = new Evaluation((TracedEntry<String>) valueEntry);
                    yield EVALUATION;
                }

                default -> {
                    
                    value = null;
                    yield NULL;
                }
            };


        }

        public Object evaluate() {

            switch (type) {

                case BOOLEAN -> {

                    Boolean boolVal = (Boolean) value;
                    return boolVal;
                }

                case LONG -> {

                    Long longVal = (Long) value;
                    return longVal.doubleValue();
                }

                case DOUBLE -> {

                    Double doubVal = (Double) value;
                    return doubVal;
                }

                case EXPRESSION -> {

                    Expression exprVal = (Expression) value;
                    return exprVal.evaluate();
                }

                case EVALUATION -> {

                    Evaluation evalVal = (Evaluation) value;
                    return evalVal.evaluate();
                }

                default -> {

                    return null;
                }
            }
        }
    
        public boolean evaluateBoolean() {

            Object result = evaluate();

            return switch (result) {

                case Boolean boolVal -> boolVal;

                case Double doubleVal -> doubleVal != 0.0;

                default -> false;
            };
        }

        public double evaluateDouble() {

            Object result = evaluate();

            return switch (result) {

                case Boolean boolVal -> boolVal ? 1.0 : 0.0;

                case Double doubleVal -> doubleVal;

                default -> 0.0;
            };
        }
    
        public static List<Boolean> evaluateBooleans(List<Value> values) {

            List<Boolean> booleanValues = Arrays.stream(values.toArray(Value[]::new))
            .map(val -> val.evaluateBoolean()).toList();

            return booleanValues;
        }

        public static List<Double> evaluateDoubles(List<Value> values) {

            List<Double> doubleValues = Arrays.stream(values.toArray(Value[]::new))
            .map(val -> val.evaluateDouble()).toList();

            return doubleValues;
        }
    }

    private static abstract class Operator {

        public static final HashMap<String, Operator> OPERATORS = createOperators();

        public abstract Object evaluate(ArrayList<Value> args);

        public abstract boolean invalidArgs(int args);

        public static final HashMap<String, Operator> createOperators() {

            HashMap<String, Operator> operators = new HashMap<>();

            operators.put("max", new Operator() {

                @Override
                public final Object evaluate(ArrayList<Value> args) {
    
                    return Value.evaluateDoubles(args).stream().max(Double::compare).get();
                }
    
                @Override
                public boolean invalidArgs(int args) {
    
                    return args < 2;
                }
            });
    
            operators.put("min", new Operator() {

                @Override
                public final Object evaluate(ArrayList<Value> args) {
    
                    return Value.evaluateDoubles(args).stream().max(Double::compare).get();
                }
    
                @Override
                public boolean invalidArgs(int args) {
    
                    return args < 2;
                }
            });
        
            return operators;
        }

        public static final Operator getOperator(TracedEntry<String> operatorEntry) throws UnexpectedValueException {

            String operatorName = operatorEntry.getValue();
    
            Operator operator = OPERATORS.get(operatorName);

            if (operator == null) {

                throw new UnexpectedValueException(operatorEntry);
            }

            return operator;
        }
    }

    private static final class Expression {

        private final Operator operator;

        private final ArrayList<Value> args = new ArrayList<>();

        @SuppressWarnings("unchecked")
        public Expression(TracedEntry<TracedDictionary> expressionEntry) throws UnexpectedValueException, ArrayLengthException, MissingPropertyException, PropertyTypeException {

            TracedDictionary expressionJson = expressionEntry.getValue();

            TracedEntry<String> operatorEntry = expressionJson.getAsString("operator", false, null);
            operator = Operator.getOperator(operatorEntry);

            TracedEntry<?> argsEntry = expressionJson.get("args", false, null);

            if (argsEntry.getValue() instanceof TracedArray argsArray) {

                if (operator.invalidArgs(argsArray.size())) {
                    
                    throw new ArrayLengthException((TracedEntry<TracedArray>) argsEntry);
                }

                for (int i : argsArray.getIndices()) {

                    args.add(new Value(argsArray.get(i, true, null)));
                }
            } else {

                if (operator.invalidArgs(1)) {
                    
                    throw new UnexpectedValueException(argsEntry);
                }
                args.add(new Value(argsEntry));
            }
        }
    }
}