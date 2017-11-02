import angine.PDP;
import angine.PIP;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Test {


    public static String pathToLua = "/home/ivan/javafun/angine/runtime/java/src/main/resources/script.lua";

    public static void run(String[] args){
        try {
            String luaPolicy = readFile(pathToLua,Charset.defaultCharset());
            PDP pdp = new PDP(luaPolicy);
            PIP pip = createTestPIP();
            testPermit(pip, pdp);
            testDeny(pip, pdp);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void testPermit(PIP pip, PDP pdp) throws Exception {
        List<String> tags = new ArrayList<String>();
        Bindings.MyUrlEntity urlEntity = new Bindings.MyUrlEntity("/index.html",tags, 5);
        List<Object> entities = new ArrayList<Object>();
        entities.add(urlEntity);



        List<String> roles = new ArrayList<String>();
        PIP.RequestContext request = new PIP.RequestContext(
                new Bindings.MySubject("user1", tags , roles, 10),
                entities,
                "GET");

        List<PIP.EvaluationContext> evaluationContexts = pip.createContext(request);
        Integer result = pdp.evaluate(evaluationContexts,false);
        if(result.equals(PDP.PERMIT)){
            System.out.println("permit success!");
        } else {
            throw new Exception("expect permit, got smth else");
        }
    }

    private static void testDeny(PIP pip, PDP pdp) throws Exception {
        List<String> tags = new ArrayList<String>();
        Bindings.MyUrlEntity urlEntity = new Bindings.MyUrlEntity("/admin/",tags, 10);
        List<Object> entities = new ArrayList<Object>();
        entities.add(urlEntity);
        List<String> roles = new ArrayList<String>();
        roles.add("user");
        PIP.RequestContext request = new PIP.RequestContext(
                new Bindings.MySubject("user1", tags , roles, 10),
                entities,
                "GET");


        List<PIP.EvaluationContext> evaluationContexts = pip.createContext(request);
        Integer result = pdp.evaluate(evaluationContexts,false);
        if(!result.equals(PDP.PERMIT)){
            System.out.println("deny success!");
        } else {
            throw new Exception("expect deny, got smth else");
        }
    }

    private static PIP createTestPIP(){

        String json = "{\n" +
                "    \"entities\" :\n" +
                "        [\n" +
                "            { \"type\" : \"subject\", \"name\" : \"user1\", \"tags\" : [],  \"roles\" : [], \"level\" : 10},\n" +
                "            { \"type\" : \"urlentity\", \"path\" : \"/index.html\", \"tags\" : [], \"level\" : 5},\n" +
                "            { \"type\" : \"urlentity\", \"path\" : \"/admin/\", \"tags\" : [], \"level\" : 10}\n" +
                "        ]\n" +
                "}";

        String schema = "{\n" +
                "    \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "    \"definitions\": {\n" +
                "        \"builtins.Entity\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        },\n" +
                "        \"builtins.Subject\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"name\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"roles\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"level\": {\n" +
                "                    \"type\": \"integer\"\n" +
                "                },\n" +
                "                \"tags\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                },\n" +
                "                \"ip\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        },\n" +
                "        \"builtins.UrlEntity\": {\n" +
                "            \"type\": \"object\",\n" +
                "            \"properties\": {\n" +
                "                \"type\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"id\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"path\": {\n" +
                "                    \"type\": \"string\"\n" +
                "                },\n" +
                "                \"level\": {\n" +
                "                    \"type\": \"integer\"\n" +
                "                },\n" +
                "                \"tags\": {\n" +
                "                    \"type\": \"array\",\n" +
                "                    \"items\": {\n" +
                "                        \"type\": \"string\"\n" +
                "                    }\n" +
                "                }\n" +
                "            },\n" +
                "            \"additionalProperties\": false\n" +
                "        }\n" +
                "    },\n" +
                "    \"type\": \"object\",\n" +
                "    \"properties\": {\n" +
                "        \"entities\": {\n" +
                "            \"type\": \"array\",\n" +
                "            \"items\": {\n" +
                "                \"oneOf\": [\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.Entity\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.UrlEntity\"\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"$ref\": \"#/definitions/builtins.Subject\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"required\": [\n" +
                "        \"entities\"\n" +
                "    ],\n" +
                "    \"additionalProperties\": false\n" +
                "}";
        return PIP.fromJson(schema,json, new Bindings.MyFactory());
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


}
