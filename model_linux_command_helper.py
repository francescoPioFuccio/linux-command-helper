from transformers import T5ForConditionalGeneration, T5Tokenizer

# Carica il modello e il tokenizer
model_name = "t5-small"
model = T5ForConditionalGeneration.from_pretrained(model_name)
tokenizer = T5Tokenizer.from_pretrained(model_name)

# Prepara l'input
input_text = "Translate English to French: How are you?"
inputs = tokenizer(input_text, return_tensors="pt")

# Genera la risposta
outputs = model.generate(inputs["input_ids"], max_length=40, num_beams=4, no_repeat_ngram_size=2)

# Decodifica l'output
output_text = tokenizer.decode(outputs[0], skip_special_tokens=True)

print(output_text)
