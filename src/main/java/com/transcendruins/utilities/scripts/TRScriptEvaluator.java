package com.transcendruins.utilities.scripts;

import java.util.HashMap;

import com.transcendruins.utilities.exceptions.propertyexceptions.UnexpectedValueException;
import com.transcendruins.utilities.json.TracedEntry;

public abstract class TRScriptEvaluator {

        private static final String NUMBER_REGEX = "^[+-]?(\\d+(\\.\\d*)?|\\.\\d+)$";

        private static final HashMap<String, TRScriptEvaluator> EVALUATORS = createEvaluators();

        public abstract Object evaluate();

        public static final HashMap<String, TRScriptEvaluator> createEvaluators() {

            HashMap<String, TRScriptEvaluator> evaluators = new HashMap<>();

            evaluators.put("random()", new TRScriptEvaluator() {

                @Override
                public Double evaluate() {

                    return Math.random();
                }
            });

            return evaluators;
        }

        public static TRScriptEvaluator getEvaluator(TracedEntry<String> valueEntry) throws UnexpectedValueException {

            String evaluatorName = valueEntry.getValue();
    
            TRScriptEvaluator evaluator = EVALUATORS.get(evaluatorName);

            if (evaluator == null) {

                throw new UnexpectedValueException(valueEntry);
            }

            return evaluator;
        }

        public static Object parseEvaluator(TracedEntry<String> valueEntry) throws UnexpectedValueException {

            String stringVal = valueEntry.getValue();

            if (stringVal.matches(NUMBER_REGEX)) {

                return Double.valueOf(stringVal);
            }

            return switch (stringVal) {

                case "PI" -> Math.PI;
                case "-PI" -> -Math.PI;

                case "TAU" -> Math.TAU;
                case "-TAU" -> -Math.TAU;

                case "E" -> Math.E;
                case "-E" -> -Math.E;

                default -> getEvaluator(valueEntry);
            };
        }
    }
