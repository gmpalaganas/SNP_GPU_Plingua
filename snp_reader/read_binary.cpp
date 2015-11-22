#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <bitset>
#include <cmath>

#define LEFT 0
#define RIGHT 1

using namespace std;

struct node{
    char value;
    node *left;
    node *right;

    node(char val, node* l, node* r);
    node(char val);

    bool is_leaf();

};

class HuffmanTree{
    public:
        HuffmanTree();
        ~HuffmanTree();

        void insert(char val, node* leaf, int pos);
        string decode(string encoded);
        void destroy_tree();
        node* get_root();

    private:
        void destroy_tree(node* leaf);
        node *root;
};

int big_to_small_endian(int big_endian);
unsigned char big_to_small_endian(unsigned char big_endian);

void construct_tree(HuffmanTree& tree);

int main(){

    HuffmanTree tree;
    construct_tree(tree);

    ifstream input("output.bin", ios::binary);
    input.seekg(0, ios::end);
    int length = input.tellg();
    short in_short;
    int in_int;
    unsigned char in_char;
    input.seekg(0, ios::beg);

    cout << "File size: " << length << endl;


    cout << "===HEADER===" << endl;
    for(int i = 0; i < 7; i++){
        input.read(reinterpret_cast<char *>(&in_char), sizeof(char));
        cout << hex << (unsigned int)in_char << endl << dec;
    }

    cout << "===SUB HEADER===" << endl;
    input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
    cout << "Number of Rules: " << big_to_small_endian(in_int) << endl;
    int rule_count = big_to_small_endian(in_int);

    input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
    cout << "Number of neurons: " << big_to_small_endian(in_int) << endl;
    int neuron_count = big_to_small_endian(in_int);

    int init_config[neuron_count];
    for(int i = 0; i < neuron_count; i++){
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        init_config[i] = big_to_small_endian(in_int);
    }

    cout << "initial configuration: {";
    for(int i = 0; i < neuron_count; i++){
        cout << init_config[i];
        if(i < neuron_count -1)
            cout << ", ";
        else
            cout << "}\n";
    }

    input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
    int * input_spike_train_steps;
    int * input_spike_train_spikes;

    int input_spike_train_len = big_to_small_endian(in_int);

    if(input_spike_train_len != 0){
        input_spike_train_steps = new int[input_spike_train_len];
        input_spike_train_spikes = new int[input_spike_train_len];
    }

    for(int i = 0; i < input_spike_train_len; i++){
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        int step = big_to_small_endian(in_int);
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        int spike = big_to_small_endian(in_int);

        input_spike_train_steps[i] = step;
        input_spike_train_spikes[i] = spike;
    }

    cout << "Input Spike Train: [";

    for(int i = 0; i < input_spike_train_len; i++){
        cout << "<" << input_spike_train_steps[i] << ", " << input_spike_train_spikes[i] << ">";
        
        if(i < input_spike_train_len - 1)
            cout << ", ";
    }

    cout << "]\n";
    

    cout << "===RULES===" << endl;
    int rule_ids[rule_count];

    for(int i = 0; i < rule_count; i++){
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        rule_ids[i] = big_to_small_endian(in_int);
    }

    cout << "Rule neuron ids: [";
    for(int i = 0; i < rule_count; i++){
        cout << rule_ids[i];
        if(i < rule_count - 1)
            cout << ", ";
        else
            cout << "]\n";
    }

    string rule_regexps[rule_count];
    int rule_consumed[rule_count];
    int rule_produced[rule_count];
    int rule_delay[rule_count];

    for(int i = 0; i < rule_count; i++){
        string decoded;
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));

        int len = big_to_small_endian(in_int);
        int n_bytes = (int)(ceil((double)len / 8));
        if(n_bytes == 0){
            input.ignore(1);
            decoded = "NONE";
        }else{
            string bits;

            for(int j = 0; j < n_bytes; j++){
                input.read(reinterpret_cast<char *>(&in_char), sizeof(char));
                bits += bitset<8>(in_char).to_string();
            }

            int offset = bits.length() - len;
            decoded = tree.decode(bits.substr(offset,len));
        }

        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));

        rule_regexps[i] = decoded;
        rule_consumed[i] = big_to_small_endian(in_int);
    }

    for(int i = 0; i < rule_count; i++){
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        rule_produced[i] = big_to_small_endian(in_int);
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        rule_delay[i] = big_to_small_endian(in_int);
    }

    cout << "Rules: [\n";

    for(int i = 0; i < rule_count; i++){
        stringstream ss;
        if(rule_regexps[i] != "NONE")
            ss << rule_regexps[i] << "/";
        ss << "a*" << rule_consumed[i] << "-->" << rule_produced[i];
        if(rule_delay[i] != 0)
            ss << ";" << rule_delay[i];
        cout << ss.str();
        
        if(i < rule_count - 1)
            cout << ",\n";
        else
            cout << "\n]\n";
    }

    cout << "===LABELS===" << endl;

    string neuron_labels[neuron_count];

    for(int i = 0; i < neuron_count; i++){
        input.read(reinterpret_cast<char *>(&in_int), sizeof(int));
        int label_len = big_to_small_endian(in_int);

        for(int j = 0; j < label_len; j++)
            neuron_labels[i] += input.get();
    }

    cout << "Neuron Labels: [";
    for(int i = 0; i < neuron_count; i++){
        cout << "<" << i + 1 << ", " << neuron_labels[i] << ">";
        if(i < neuron_count - 1)
            cout << ", ";
        else
            cout << "]\n";
    }
    int matrix_size = neuron_count * neuron_count;
    int n_bytes = (int)(ceil((double)matrix_size / 8));

    string linear_matrix;

    for(int i = 0; i < n_bytes; i++){
        input.read(reinterpret_cast<char *>(&in_char), sizeof(char));
        linear_matrix += bitset<8>(in_char).to_string();
    }

    int offset = linear_matrix.length() - matrix_size;
    linear_matrix = linear_matrix.substr(offset, matrix_size);

    int matrix[neuron_count][neuron_count];

    int index = 0;
    for(int i = 0; i < neuron_count; i++){
        for(int j = 0; j < neuron_count; j++){
            matrix[i][j] = (linear_matrix[index] == '1')?1:0;
            index++;
        }
    }

    cout << "===SYNAPSES===" << endl;
    for(int i = 0; i < neuron_count; i++){
        for(int j = 0; j < neuron_count; j++){
            cout << matrix[i][j];
            if( j < neuron_count - 1)
                cout << ", ";
        }
        cout << endl;
    }

    if(input_spike_train_len != 0){
        delete[] input_spike_train_steps;
        delete[] input_spike_train_spikes;
    }
    
}

node::node(char val, node* l, node* r): value(val), left(l), right(r) { }

node::node(char val): value(val) {
    left = NULL;
    right = NULL;
}

HuffmanTree::HuffmanTree(){
    root = new node('_');
}

HuffmanTree::~HuffmanTree(){
    destroy_tree();
}

void HuffmanTree::destroy_tree(node *leaf){
    if(leaf != NULL){
        destroy_tree(leaf->left);
        destroy_tree(leaf->right);
        delete leaf;
    }
}

void HuffmanTree::destroy_tree(){
    destroy_tree(root);
}

void HuffmanTree::insert(char value, node* leaf, int pos){
    if(leaf != NULL){
        if(pos == 0)
            leaf->left = new node(value);
        else
            leaf->right = new node(value);
    }
}

node* HuffmanTree::get_root(){
    return root;
}

string HuffmanTree::decode(string encoded){
    stringstream ss;    

    node* cur_node = root;
    for(int i = 0; i < encoded.length(); i++){

        if(cur_node->left == NULL && cur_node->right == NULL)
            return ss.str(); 

        if(encoded[i] == '0')
            cur_node = cur_node->left;
        else
            cur_node = cur_node->right;

        if(cur_node->value != '_'){
            ss << cur_node->value;
            cur_node = root;
        }

    }

    return ss.str();
}

void construct_tree(HuffmanTree& tree){

    tree.insert('_', tree.get_root(), 0); 
    tree.insert('_', tree.get_root(), 1); 

    // Process left half of the tree
    node *cur_node = tree.get_root()->left;

    cur_node->left = new node('_');
    cur_node->left->left = new node('2');
    cur_node->left->right = new node('1');

    cur_node->right = new node('_');
    cur_node->right->left = new node('+');

    cur_node = cur_node->right;
    cur_node->right = new node('_');

    cur_node = cur_node->right;
    cur_node->left = new node('6');
    cur_node->right = new node('_');

    cur_node = cur_node->right;
    cur_node->left = new node('9');
    cur_node->right = new node('_');

    cur_node = cur_node->right;
    cur_node->right = new node('0');
    cur_node->left = new node('_');

    cur_node = cur_node->left;
    cur_node->left = new node('(');
    cur_node->right = new node(')');

    //Process right half of the tree
    cur_node = tree.get_root()->right;

    cur_node->left = new node('_');
    cur_node->right = new node('_');

    cur_node->left->left = new node('*');
    cur_node->left->right = new node('a');

    cur_node = cur_node->right;
    cur_node->left = new node('_');
    cur_node->right = new node('_');

    cur_node->left->left = new node('5');
    cur_node->left->right = new node('4');

    cur_node = cur_node->right;
    cur_node->right = new node('3');
    cur_node->left = new node('_');

    cur_node = cur_node->left;
    cur_node->right = new node('8');
    cur_node->left = new node('7');

}

int big_to_small_endian(int big_endian){

    int small_endian;

    unsigned char *small_endian_bytes = (unsigned char *)&small_endian;
    unsigned char *big_endian_bytes = (unsigned char *)&big_endian;

    for(int i = 0; i < sizeof(int); i++)
        small_endian_bytes[i] = big_endian_bytes[sizeof(int) - i - 1];

    return small_endian;

}

unsigned char big_to_small_endian(unsigned char big_endian){

    unsigned char small_endian;

    unsigned char * small_endian_bytes = &small_endian;
    unsigned char *big_endian_bytes = &big_endian;

    for(int i = 0; i < sizeof(int); i++)
        small_endian_bytes[i] = big_endian_bytes[sizeof(int) - i - 1];

    return small_endian;



}
