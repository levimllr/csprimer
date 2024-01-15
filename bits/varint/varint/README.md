In this exercise, you will implement the [Base 128 Varint](https://protobuf.dev/programming-guides/encoding/#varints) encoding used in Protocol Buffers. This is mostly an excuse to play with a fun data encoding, while exposing you to some basic concepts in working with binary data such as reading hexadecimal, byte ordering and bitwise operations.

You should watch the introductory section of the video for some context on what this problem is, and what it means to solve it. In short, you should write an `encode` function which takes an unsigned 64 bit integer and returns a sequence of bytes in the varint encoding that Protocol Buffers uses. You should also write a `decode` function which does the inverse.

Some example inputs are provided in the exercise files, as unsigned 64 bit integers, in case it's hard to construct these as literals in your language of choice (you are welcome to use any; I use Python in the solution above).

A simple test of correctness in Python might look like:

```
assert encode(150) == b'\x96\x01'
assert decode(b'\x96\x01') == 150
```

You could also roundtrip test randomly, or a range of inputs, e.g. to test the first ~1 billion inputs:

```
for n in range(1 << 30):
assert decode(encode(n)) == n
```

If you get stuck, you could:

- Watch or re-watch any related explainer videos
- Watch more of the solution, for instance the planning step
- Ask questions below or on Discord
- See how I implement the encoder, and implement the decoder yourself
- Watch the solution but implement it yourself from scratch tomorrow
- Translate my solution into another language

If you are looking for a stretch goal, you could:

- Support decoding of multiple adjacent varints
- Understand and implement protobuf's `sintN` type using [ZigZag encoding](https://protobuf.dev/programming-guides/encoding/#signed-ints)

Good luck!